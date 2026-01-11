INSERT INTO article (title, content, author, created_at, updated_at)
VALUES ('제목 1', '내용 1', 'user1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO article (title, content, author, created_at, updated_at)
VALUES ('제목 2', '내용 2', 'user2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO article (title, content, author, created_at, updated_at)
VALUES ('제목 3', '내용 3', 'user3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (email, password, nickname, role)
VALUES (
  'admin@santa.com',
  '$2a$10$n.V3lqz4p9mHxw/uhGVXgOOtqnn5Y43pfNozSxM.kCRuUBB5EBihu',
  '관리자',
  'ADMIN'
);
