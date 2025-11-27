------------------------------------------------------------
-- 팀(Team) 더미 데이터
------------------------------------------------------------
INSERT INTO team (name) VALUES
    ('개발팀'),
    ('기획팀'),
    ('디자인팀');
-- => 여기서 생성되는 id(또는 team_id)는 1, 2, 3 이 됨

------------------------------------------------------------
-- 직원(Employee) 더미 데이터
-- level 은 ENUM('SENIOR','JUNIOR', ...) 가정
------------------------------------------------------------
INSERT INTO employee (name, email, team_id, level) VALUES
    ('더미 개발자', 'pcw1405@naver.com',    1, 'SENIOR'),
    ('더미 기획자', 'plan@dummy.com',   2, 'JUNIOR'),
    ('더미 디자이너', 'design@dummy.com', 3, 'JUNIOR');
-- => employee PK는 1, 2, 3

------------------------------------------------------------
-- 회원(Member) 더미 데이터
-- 실제 Member 엔티티/DDL 에 맞게 테이블/컬럼명은 이미 맞다고 가정
-- 비밀번호 인코딩 로직이 있다면, 애플리케이션에서 인코딩하도록
------------------------------------------------------------
-- 사내 직원용 계정 (Employee id=1 과 매핑)
--INSERT INTO member (name, email, password, role, employee_id) VALUES
--    ('사내개발자계정', 'pcw1405@naver.com', '12345678', 'ROLE_EMPLOYEE', 1);

-- 직원이 아닌 일반 회원(외부 고객) 예시 (employee_id = NULL 허용 가정)
--INSERT INTO member (name, email, password, role, employee_id) VALUES
--    ('외부고객', 'customer@example.com', '12345678', 'ROLE_USER', NULL);

------------------------------------------------------------
-- 게시판 타입(BoardType) 더미 데이터
------------------------------------------------------------
INSERT INTO board_type (name, description) VALUES
    ('사내 공지', '사내 직원 공지용 게시판'),
    ('팀 게시판', '팀 단위 협업/자료 공유 게시판'),
    ('외부 공지', '외부 고객에게 공개되는 공지 게시판');
-- => board_type PK는 1, 2, 3

------------------------------------------------------------
-- 게시판 권한(BoardPermission) 더미 데이터
-- 컬럼: (id 자동 증가), permission, board_type_id, employee_id, team_id
------------------------------------------------------------
-- 사내 공지(1번) READ 권한: 각 팀(1,2,3)에게 READ 부여
INSERT INTO board_permission (permission, board_type_id, employee_id, team_id) VALUES
    ('READ', 1, NULL, 1),
    ('READ', 1, NULL, 2),
    ('READ', 1, NULL, 3);

-- 팀 게시판(2번) WRITE 권한: 개발팀(1번 팀)에게 WRITE 부여 (예시)
INSERT INTO board_permission (permission, board_type_id, employee_id, team_id) VALUES
    ('WRITE', 2, NULL, 1);

-- 외부 공지(3번)는 READ 권한 레코드 없음
-- → isBoardPublic(3L) == true 로 동작

------------------------------------------------------------
-- 게시글(Post) 더미 데이터
-- 엔티티 기준 컬럼:
--   (id 자동 증가), title, content, board_type_id, employee_id,
--   view_count, reply_count ...
------------------------------------------------------------
-- 사내 공지 게시판(1번)에 올라온 글들
INSERT INTO post (title, content, board_type_id, employee_id, view_count, reply_count) VALUES
    ('사내 개발 공지', '개발팀 공지입니다.',             1, 1, 0, 0),
    ('전사 공지 사항', '전 직원 대상 공지입니다.',        1, 1, 0, 0);

-- 팀 게시판(2번)에 올라온 글들
INSERT INTO post (title, content, board_type_id, employee_id, view_count, reply_count) VALUES
    ('기획 회의록',   '기획팀 회의 내용을 정리한 문서입니다.', 2, 2, 0, 0),
    ('디자인 가이드', '디자인 시스템 및 색상 팔레트입니다.',   2, 3, 0, 0);

-- 외부 공지 게시판(3번)에 올라온 글들
INSERT INTO post (title, content, board_type_id, employee_id, view_count, reply_count) VALUES
    ('서비스 점검 안내', '2025-12-01 01:00 ~ 03:00 시스템 점검 예정입니다.', 3, 1, 0, 0),
    ('신규 기능 오픈', '신규 결제 기능이 오픈되었습니다.',                3, 1, 0, 0);
-- => post PK는 순서대로 1,2,3,4,5,6 이 될 것

------------------------------------------------------------
-- 게시글 개별 권한(PostPermission) 더미 데이터
-- 컬럼: (id 자동 증가), permission, post_id, employee_id, team_id
------------------------------------------------------------
-- 기획 회의록(post_id = 3)은 기획팀(2번)만 READ
INSERT INTO post_permission (permission, post_id, employee_id, team_id) VALUES
    ('READ', 3, NULL, 2);

-- 디자인 가이드(post_id = 4)는 디자인팀(3번)만 READ
INSERT INTO post_permission (permission, post_id, employee_id, team_id) VALUES
    ('READ', 4, NULL, 3);

-- 사내 개발 공지(post_id = 1)에 대해 개발팀(1번) WRITE 권한 예시
INSERT INTO post_permission (permission, post_id, employee_id, team_id) VALUES
    ('WRITE', 1, NULL, 1);