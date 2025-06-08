INSERT INTO team (id, name) VALUES
(1, '개발팀'),
(2, '기획팀'),
(3, '디자인팀');

-- 직원 (author_id FK 해결용 더미)
INSERT INTO employee (id, name, email, team_id, level) VALUES
(1, '더미 개발자', 'dev@dummy.com', 1, 'SENIOR'),
(2, '더미 기획자', 'plan@dummy.com', 2, 'JUNIOR'),
(3, '더미 디자이너', 'design@dummy.com', 3, 'JUNIOR');

-- 글
INSERT INTO post (id, title, content, author_id) VALUES
(1, '개발 가이드', '개발자 전용 기술 문서입니다.', 1),
(2, '기획 회의록', '기획팀 회의 내용을 정리한 문서입니다.', 2),
(3, '디자인 시스템', '디자인 가이드라인 및 색상 팔레트입니다.', 3);

-- 권한
INSERT INTO post_permission (id, post_id, employee_id, team_id, permission_type) VALUES
(1, 1, 1, 1, 'VIEW'),
(2, 2, 2, 2, 'VIEW'),
(3, 3, 3, 3, 'VIEW'),
(4, 1, 1, 1, 'EDIT');