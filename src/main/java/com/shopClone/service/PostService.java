package com.shopClone.service;

import com.shopClone.constant.PermissionType;
import com.shopClone.entity.BoardType;
import com.shopClone.entity.Employee;
import com.shopClone.entity.Member;
import com.shopClone.entity.Post;
import com.shopClone.repository.BoardPermissionRepository;
import com.shopClone.repository.BoardTypeRepository;
import com.shopClone.repository.PostPermissionRepository;
import com.shopClone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardPermissionRepository boardPermissionRepository;
    private final PostPermissionRepository postPermissionRepository;
    private final BoardTypeRepository boardTypeRepository;

    /** 컨트롤러에서 로그인 Member를 받아 권한 반영 목록 제공 (간단 버전: 첫 페이지 20건) */
    public List<Post> findReadablePosts(Member member, Long boardId) {
        boolean boardPublic = isBoardPublic(boardId);


        // 1) 로그인은 했지만 직원 정보가 없는 계정
        if (member == null || member.getEmployee() == null) {
            if (!boardPublic) return List.of();


        // ✅ 변경: 공개 게시판이라도 PostPermission(READ)로 제한된 글은 제외
            Page<Post> page = postRepository.findPublicVisiblePosts(
                    boardId,
                    PermissionType.READ,
                    PageRequest.of(0, 20)
            );
            return page.getContent();
        }


        // 2) 정상 직원 계정
        Employee emp = member.getEmployee();
        Long empId = emp.getId();
//        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : null;
        // -1L은 안쓰는 것이 좋다...

        Page<Post> page = postRepository.findVisiblePosts(
                boardId,
                empId,
                teamId,
                boardPublic,
                PermissionType.READ,
                PageRequest.of(0, 20)
        );
        return page.getContent();
    }

    private boolean hasPostPermissionByTeam(Long postId, PermissionType p, Long teamId) {
        if (teamId == null) return false;
        return postPermissionRepository.existsByPostIdAndPermissionAndTeamId(postId, p, teamId);
    }
//exists 성능적으로 볼 때 exists는 가볍다 ....
    private boolean hasBoardPermissionByTeam(Long boardId, PermissionType p, Long teamId) {
        if (teamId == null) return false;
        return boardPermissionRepository.existsByBoardType_IdAndTeam_IdAndPermission(boardId, teamId, p);
    }



    /** 게시판이 공개판인지(READ 권한 레코드가 없는지) */
    private boolean isBoardPublic(Long boardId) {
        long cnt = boardPermissionRepository.countByBoardType_IdAndPermission(boardId, PermissionType.READ);
        return cnt == 0L;
    }


    public boolean canView(Post post, Member member) {
        Long boardId = post.getBoardType().getId();
        boolean boardPublic = isBoardPublic(boardId);


    // employee 확보
            Employee emp = (member == null) ? null : member.getEmployee();


    // (A) employee가 없는 경우
            if (emp == null) {
                if (!boardPublic) return false;


    // ✅ 이 글이 PostPermission(READ)로 제한되어 있으면 외부/미등록은 불가
                boolean hasReadRestrictions = postPermissionRepository.existsByPostIdAndPermission(post.getId(), PermissionType.READ);
                return !hasReadRestrictions;
            }


            Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : null;
//            Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;


    // 1) 작성자
            if (post.getAuthor() != null && empId.equals(post.getAuthor().getId())) {
                return true;
            }


    // 2) 이 글이 READ 제한(화이트리스트)인가?
            boolean hasReadRestrictions = postPermissionRepository.existsByPostIdAndPermission(post.getId(), PermissionType.READ);
            if (hasReadRestrictions) {
    // ✅ 제한이 걸려있으면, 명단 매칭만 통과
                return postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(post.getId(), PermissionType.READ, empId)
                        || hasPostPermissionByTeam(post.getId(), PermissionType.READ, teamId);
            }
        boolean hasPostWhitelist =
                postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(
                        post.getId(), PermissionType.READ, empId)
                        || hasPostPermissionByTeam(post.getId(), PermissionType.READ, teamId);

    // 3) 제한이 없으면 보드 정책
            if (boardPublic) return true;


    // 4) 제한 보드면 BoardPermission(READ) 필요
            return canReadBoardBySubject(boardId, empId, teamId);
        }


    /** 열람 권한 */
    public boolean canView(Post post, Employee emp) {
        Long empId = emp.getId();
//        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : null;
        // 1) 작성자 본인은 항상 가능
        if (post.getAuthor() != null && empId.equals(post.getAuthor().getId())) {
            return true;
        }

        // 2) 글에 READ PostPermission이 있는가?
        boolean hasPostWhitelist =
                postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(
                        post.getId(), PermissionType.READ, empId)
                        || hasPostPermissionByTeam(post.getId(), PermissionType.READ, teamId);

        if (hasPostWhitelist) {
            return true;
        }

        // 3) 없으면 보드 규칙 상속
        boolean boardPublic = isBoardPublic(post.getBoardType().getId());
        if (boardPublic) {
            return true;
        }

        // 4) 제한 보드면 BoardPermission(READ) 필요
        long canReadBoard =
                boardPermissionRepository.countByBoardType_IdAndPermission(
                        post.getBoardType().getId(), PermissionType.READ);
        if (canReadBoard == 0) {
            return false;
        }

        // 5) 이 보드의 READ 명단에 속하는가? (직원/팀 기준)
        return canReadBoardBySubject(post.getBoardType().getId(), empId, teamId);
    }
    public boolean canEdit(Post post, Member member) {
        Employee emp = (member == null) ? null : member.getEmployee();
        if (emp == null) return false;
        return canEdit(post, emp); // 기존 로직 재사용
    } // 오버로드 추가, employee null 계정에서 절대 터지지 않음

    /** 수정 권한: 작성자 또는 WRITE PostPermission / BoardPermission */
    public boolean canEdit(Post post, Employee emp) {
        Long empId = emp.getId();
//        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : null;

        // 1) 작성자
        if (post.getAuthor() != null && empId.equals(post.getAuthor().getId())) {
            return true;
        }

        // 2) 글 단위 WRITE 권한
        boolean canByPost =
                postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(
                        post.getId(), PermissionType.WRITE, empId)
                        || hasPostPermissionByTeam(post.getId(), PermissionType.WRITE, teamId);

        if (canByPost) {
            return true;
        }

        // 3) 보드 WRITE 권한 (BoardPermission) – 아직 데모 용
        return canWriteBoardBySubject(post.getBoardType().getId(), empId, teamId);
    }

    @Transactional
    public void updatePost(Long id, String title, String content, Member member) {
        Post post = findPostById(id);

        if (!canEdit(post, member)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
    }

    // ============================================================
    // 5) BoardPermission 체크 더미 제거(실제 구현)
    // ============================================================


        private boolean canReadBoardBySubject(Long boardId, Long empId, Long teamId) {
            boolean byEmp = boardPermissionRepository.existsByBoardType_IdAndEmployee_IdAndPermission(
                    boardId, empId, PermissionType.READ);
            boolean byTeam = hasBoardPermissionByTeam(boardId, PermissionType.READ, teamId);
            return byEmp || byTeam;
        }


    private boolean canWriteBoardBySubject(Long boardId, Long empId, Long teamId) {

        long writeCount = boardPermissionRepository.countByBoardType_IdAndPermission(
                boardId, PermissionType.WRITE
        );

        // ✅ WRITE 명단이 없으면: 로그인한 직원이면 누구나 작성 가능
        if (writeCount == 0) {
            return true;
        }

        // ✅ WRITE 명단이 있으면: 명단 매칭만 허용
        boolean byEmp = boardPermissionRepository.existsByBoardType_IdAndEmployee_IdAndPermission(
                boardId, empId, PermissionType.WRITE
        );

        boolean byTeam = (teamId != null) && boardPermissionRepository.existsByBoardType_IdAndTeam_IdAndPermission(
                boardId, teamId, PermissionType.WRITE
        );

        return byEmp || byTeam;
    }

    public boolean canWriteBoard(Long boardId, Member member) {
        if (member == null || member.getEmployee() == null) return false;

        Employee emp = member.getEmployee();
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : null; // ✅ null 통일

        return canWriteBoardBySubject(boardId, empId, teamId);
    }

    @Transactional
    public Long createPost(Long boardId, String title, String content, Member member) {

        if (member == null || member.getEmployee() == null) {
            throw new SecurityException("작성 권한이 없습니다.");
        }

        BoardType boardType = boardTypeRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다. id=" + boardId));

        Employee emp = member.getEmployee();
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : null; // ✅ null 통일

        if (!canWriteBoardBySubject(boardId, empId, teamId)) {
            throw new SecurityException("작성 권한이 없습니다.");
        }

        Post post = new Post();
        post.setBoardType(boardType);
        post.setAuthor(emp);
        post.setTitle(title);
        post.setContent(content);

        return postRepository.save(post).getId();
    }

//    private boolean canReadBoardBySubject(Long boardId, Long empId, Long teamId) {
//        // 실제 구현 시:
//        // return boardPermissionRepository.existsByBoardTypeIdAndPermissionAndEmployeeId(boardId, PermissionType.READ, empId)
//        //     || boardPermissionRepository.existsByBoardTypeIdAndPermissionAndTeamId(boardId, PermissionType.READ, teamId);
//        return true; // 데모
//    }
//
//    private boolean canWriteBoardBySubject(Long boardId, Long empId, Long teamId) {
//        // 실제 구현 시:
//        // return boardPermissionRepository.existsByBoardTypeIdAndPermissionAndEmployeeId(boardId, PermissionType.WRITE, empId)
//        //     || boardPermissionRepository.existsByBoardTypeIdAndPermissionAndTeamId(boardId, PermissionType.WRITE, teamId);
//        return false; // 데모
//    }

    private Long chooseDefaultBoardId() {
        return boardTypeRepository.findAll().stream()
                .findFirst()
                .map(BoardType::getId)
                .orElseThrow(() -> new IllegalStateException("등록된 게시판이 없습니다."));
    }

    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
    }
}