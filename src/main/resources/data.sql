INSERT INTO team (id, name) VALUES
(1, '개발팀'),
(2, '기획팀'),
(3, '디자인팀');

INSERT INTO employee (id, name, email, team_id, level) VALUES
(1, '더미 개발자', 'dev@dummy.com', 1, 'SENIOR'),
(2, '더미 기획자', 'plan@dummy.com', 2, 'JUNIOR'),
(3, '더미 디자이너', 'design@dummy.com', 3, 'JUNIOR');

-- 필요하다면 board_type 더미도 추가 (예: id=1)
-- INSERT INTO board_type (id, name) VALUES (1, '기본 게시판');

INSERT INTO post (id, title, content, employee_id, view_count, reply_count) VALUES
(1, '개발 가이드', 'developer 전용 기술 문서입니다.', 1, 0, 0),
(2, '기획 회의록', '기획팀 회의 내용을 정리한 문서입니다.', 2, 0, 0),
(3, '디자인 시스템', '디자인 가이드라인 및 색상 팔레트입니다.', 3, 0, 0);

INSERT INTO post_permission (id, post_id, employee_id, team_id, permission)
VALUES
(1, 1, 1, 1, 'READ'),
(2, 2, 2, 2, 'READ'),
(3, 3, 3, 3, 'READ'),
(4, 1, 1, 1, 'WRITE');