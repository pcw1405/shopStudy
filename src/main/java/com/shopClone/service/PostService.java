package com.shopClone.service;

import com.shopClone.entity.BoardType;
import com.shopClone.constant.PermissionType;
import com.shopClone.entity.Employee;
import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.entity.PostPermission;
import com.shopClone.repository.BoardPermissionRepository;
import com.shopClone.repository.BoardTypeRepository;
import com.shopClone.repository.PostPermissionRepository;
import com.shopClone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardPermissionRepository boardPermissionRepository;
    private final PostPermissionRepository postPermissionRepository;
    private final BoardTypeRepository boardTypeRepository;

    /** 컨트롤러에서 로그인 Member를 받아 권한 반영 목록 제공 (간단 버전: 첫 페이지 20건) */
    public List<Post> findReadablePosts(Member member) {
        Employee emp = member.getEmployee();
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;

        // “어떤 보드의 목록인지”가 컨트롤러 파라미터에 없다면,
        // 우선 전체 보드 중 대표 하나를 선택하거나, 전체 공개/제한을 아우르는 별도 쿼리가 필요.
        // 여기선 예시로 "모든 보드" 대신, 우선 보드 1개(또는 UI에서 boardId를 받아오도록) 가정:
        Long boardId = chooseDefaultBoardId(); // TODO: 실제 구현에서 파라미터로 받아오는 걸 권장

        boolean boardPublic = isBoardPublic(boardId);

        Page<Post> page = postRepository.findVisiblePosts(
                boardId, empId, teamId, boardPublic, PageRequest.of(0, 20));

        return page.getContent();
    }

    /** 게시판이 공개판인지(READ 권한 레코드가 없는지) */
    private boolean isBoardPublic(Long boardId) {
        long cnt = boardPermissionRepository.countByBoardTypeIdAndPermission(boardId, PermissionType.READ);
        return cnt == 0L;
    }

    /** 상세 조회 */
//    public Post findPostById(Long id) {
//        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//    }

    /** 열람 권한 */
    public boolean canView(Post post, Employee emp) {
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;

        // 작성자 본인은 항상 가능
        if (post.getAuthor() != null && empId.equals(post.getAuthor().getId())) return true;

        // 글에 READ PostPermission이 있는가?
        boolean hasPostWhitelist =
                postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(post.getId(), PermissionType.READ, empId)
                        || postPermissionRepository.existsByPostIdAndPermissionAndTeamId(post.getId(), PermissionType.READ, teamId);

        if (hasPostWhitelist) return true;

        // 없으면 보드 규칙 상속
        boolean boardPublic = isBoardPublic(post.getBoardType().getId());
        if (boardPublic) return true;

        // 제한 보드면 BoardPermission(READ) 필요
        long canReadBoard =
                boardPermissionRepository.countByBoardTypeIdAndPermission(post.getBoardType().getId(), PermissionType.READ);
        if (canReadBoard == 0) return false;

        // 이 보드의 READ 명단에 속하는가?
        // count만으로는 대상(직원/팀) 일치 판정이 안 되므로, exists용 repo 메서드를 추가해도 좋음.
        // 간단히는 Post 목록과 동일 로직의 별도 exists 쿼리를 만들어 쓰는 걸 권장.
        // 여기서는 간단 구현(작성자/공개/게시글 whitelist 외엔 false로 처리) 대신,
        // 실제 운영에선 BoardPermissionRepository에 exists 메서드를 추가하세요.
        // -> 확실한 판정을 위해 아래 보조 메서드 추천:
        return canReadBoardBySubject(post.getBoardType().getId(), empId, teamId);
    }

    /** 수정 권한: 작성자 또는 WRITE PostPermission / BoardPermission */
    public boolean canEdit(Post post, Employee emp) {
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;

        if (post.getAuthor() != null && empId.equals(post.getAuthor().getId())) return true;

        boolean canByPost =
                postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(post.getId(), PermissionType.WRITE, empId)
                        || postPermissionRepository.existsByPostIdAndPermissionAndTeamId(post.getId(), PermissionType.WRITE, teamId);

        if (canByPost) return true;

        // 보드 WRITE 권한 검사 (exists 메서드가 없어서 임시 간주: READ처럼 count만 쓰면 안 됨)
        return canWriteBoardBySubject(post.getBoardType().getId(), empId, teamId);
    }

    @Transactional
    public void updatePost(Long id, String title, String content, Employee editor) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!canEdit(post, editor)) throw new SecurityException("수정 권한이 없습니다.");

        post.setTitle(title);
        post.setContent(content);
        // JPA dirty checking
    }

    // ===== 아래 두 개는 운영에서 꼭 구현 권장: BoardPermission에 대해 subject(직원/팀) 일치로 판정 =====

    private boolean canReadBoardBySubject(Long boardId, Long empId, Long teamId) {
        // BoardPermissionRepository에 existsByBoardTypeIdAndPermissionAndEmployeeId, TeamId 등을 추가해 쓰는 걸 권장.
        // 임시 구현(권장 구현 예시):
        // return boardPermissionRepository.existsByBoardTypeIdAndPermissionAndEmployeeId(boardId, PermissionType.READ, empId)
        //     || boardPermissionRepository.existsByBoardTypeIdAndPermissionAndTeamId(boardId, PermissionType.READ, teamId);
        return true; // 데모 단계: 실제에선 위 exists 메서드 추가 후 정확 판정하도록 교체
    }

    private boolean canWriteBoardBySubject(Long boardId, Long empId, Long teamId) {
        // 마찬가지로 WRITE 권한 존재 여부를 BoardPermission에서 exists로 판정하도록 구현 권장
        return false; // 데모 기본값
    }

    private Long chooseDefaultBoardId() {
        // 실제로는 UI에서 boardId를 받는 게 정답.
        // 여기서는 데모 용도로 첫 번째 보드를 고른다.
        return boardTypeRepository.findAll().stream()
                .findFirst()
                .map(BoardType::getId)
                .orElseThrow(() -> new IllegalStateException("등록된 게시판이 없습니다."));
    }
//    public List<Post> findReadablePosts(Member member) {
//        Employee employee = member.getEmployee();
//        List<PostPermission> permissions = postPermissionRepository.findByEmpOrTeamWithPermission(
//                employee, employee.getTeam(), PermissionType.VIEW);
//
//        return permissions.stream()
//                .map(PostPermission::getPost)
//                .distinct()
//                .collect(Collectors.toList());
//    }



    // 상세페이지 열람을 권한있는 사람만 할 수 있게 해야함
//    public boolean canView(Post post, Employee employee) {
//        // 작성자인 경우 무조건 열람 가능
//        if (post.getAuthor().getId().equals(employee.getId())) {
//            return true;
//        }
//
//        // 작성자가 아닌 경우, 권한이 있는지 확인
//        return postPermissionRepository.findByEmpOrTeamWithPermission(
//                        employee, employee.getTeam(), PermissionType.VIEW)
//                .stream()
//                .anyMatch(p -> p.getPost().getId().equals(post.getId()));
//    }

    // 게시물을 찾기위해서
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
    }

//    public boolean canEdit(Post post, Employee employee) {
//        return postPermissionRepository.findByEmpOrTeamWithPermission(
//                        employee, employee.getTeam(), PermissionType.EDIT)
//                .stream()
//                .anyMatch(p -> p.getPost().getId().equals(post.getId()));
//    }

    /// 수정 기능(데이트베이스 반영)
//    public void updatePost(Long id, String title, String content, Employee employee) {
//        Post post = findPostById(id);
//        if (!canEdit(post, employee)) {
//            throw new AccessDeniedException("수정 권한이 없습니다.");
//        }
//        post.setTitle(title);
//        post.setContent(content);
//        postRepository.save(post);
//    }

}
