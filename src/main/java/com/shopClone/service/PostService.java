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
            if (boardPublic) {
                Page<Post> page = postRepository.findByBoardTypeId(
                        boardId,
                        PageRequest.of(0, 20)
                );
                return page.getContent();
            }
            return List.of();
        }

        // 2) 정상 직원 계정
        Employee emp = member.getEmployee();
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;

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


//    public List<Post> findReadablePosts(Member member) {
//
//        Long boardId = chooseDefaultBoardId();
//        boolean boardPublic = isBoardPublic(boardId);
//
//        // 1) 로그인은 했지만 직원 정보가 없는 계정 (일반 회원 / 외부 고객)
//        if (member == null || member.getEmployee() == null) {
//            // 기본 게시판이 공개면 그냥 전체 목록 보여주기
//            if (boardPublic) {
//                Page<Post> page = postRepository.findByBoardTypeId(
//                        boardId,
//                        PageRequest.of(0, 20)
//                );
//                return page.getContent();
//            }
//            // 비공개 보드는 직원만 보도록 비워서 리턴
//            return List.of();
//        }
//
//        // 2) 정상적인 직원 계정
//        Employee emp = member.getEmployee();
//        Long empId = emp.getId();
//        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;
//
//        Page<Post> page = postRepository.findVisiblePosts(
//                boardId,
//                empId,
//                teamId,
//                boardPublic,
//                PermissionType.READ,          // ✅ readPermission 파라미터
//                PageRequest.of(0, 20)
//        );
//
//        return page.getContent();
//    }

    public List<Post> findReadablePosts_demo(Member member) {
        Employee emp = member.getEmployee();
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;



        Long boardId = chooseDefaultBoardId();
        boolean boardPublic = isBoardPublic(boardId);

        Page<Post> page = postRepository.findVisiblePosts(
                boardId,
                empId,
                teamId,
                boardPublic,
                PermissionType.READ,          // ✅ readPermission 파라미터
                PageRequest.of(0, 20)
        );

        return page.getContent();
    }
    //

//    public List<Post> findReadablePosts(Member member, Long boardId) {
//
//        Long targetBoardId = (boardId != null)
//                ? boardId
//                : chooseDefaultBoardId();
//
//        boolean boardPublic = isBoardPublic(targetBoardId);
//
//        // 직원 아닌 계정 처리 생략...
//
//        Employee emp = member.getEmployee();
//        Long empId = emp.getId();
//        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;
//
//        Page<Post> page = postRepository.findVisiblePosts(
//                targetBoardId,
//                empId,
//                teamId,
//                boardPublic,
//                PermissionType.READ,
//                PageRequest.of(0, 20)
//        );
//
//        return page.getContent();
//    }

    /** 게시판이 공개판인지(READ 권한 레코드가 없는지) */
    private boolean isBoardPublic(Long boardId) {
        long cnt = boardPermissionRepository.countByBoardType_IdAndPermission(boardId, PermissionType.READ);
        return cnt == 0L;
    }

    /** 열람 권한 */
    public boolean canView(Post post, Employee emp) {
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;

        // 1) 작성자 본인은 항상 가능
        if (post.getAuthor() != null && empId.equals(post.getAuthor().getId())) {
            return true;
        }

        // 2) 글에 READ PostPermission이 있는가?
        boolean hasPostWhitelist =
                postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(
                        post.getId(), PermissionType.READ, empId)
                        || postPermissionRepository.existsByPostIdAndPermissionAndTeamId(
                        post.getId(), PermissionType.READ, teamId);

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

    /** 수정 권한: 작성자 또는 WRITE PostPermission / BoardPermission */
    public boolean canEdit(Post post, Employee emp) {
        Long empId = emp.getId();
        Long teamId = (emp.getTeam() != null) ? emp.getTeam().getId() : -1L;

        // 1) 작성자
        if (post.getAuthor() != null && empId.equals(post.getAuthor().getId())) {
            return true;
        }

        // 2) 글 단위 WRITE 권한
        boolean canByPost =
                postPermissionRepository.existsByPostIdAndPermissionAndEmployeeId(
                        post.getId(), PermissionType.WRITE, empId)
                        || postPermissionRepository.existsByPostIdAndPermissionAndTeamId(
                        post.getId(), PermissionType.WRITE, teamId);

        if (canByPost) {
            return true;
        }

        // 3) 보드 WRITE 권한 (BoardPermission) – 아직 데모 용
        return canWriteBoardBySubject(post.getBoardType().getId(), empId, teamId);
    }

    @Transactional
    public void updatePost(Long id, String title, String content, Employee editor) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!canEdit(post, editor)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
        // JPA dirty checking
    }

    private boolean canReadBoardBySubject(Long boardId, Long empId, Long teamId) {
        // 실제 구현 시:
        // return boardPermissionRepository.existsByBoardTypeIdAndPermissionAndEmployeeId(boardId, PermissionType.READ, empId)
        //     || boardPermissionRepository.existsByBoardTypeIdAndPermissionAndTeamId(boardId, PermissionType.READ, teamId);
        return true; // 데모
    }

    private boolean canWriteBoardBySubject(Long boardId, Long empId, Long teamId) {
        // 실제 구현 시:
        // return boardPermissionRepository.existsByBoardTypeIdAndPermissionAndEmployeeId(boardId, PermissionType.WRITE, empId)
        //     || boardPermissionRepository.existsByBoardTypeIdAndPermissionAndTeamId(boardId, PermissionType.WRITE, teamId);
        return false; // 데모
    }

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