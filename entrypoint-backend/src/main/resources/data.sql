DELETE FROM users;
DELETE FROM group_roles;
DELETE FROM usergroups;
DELETE FROM roles;

INSERT INTO roles (id, name) VALUES (1, 'CREATE_EVENT');
INSERT INTO roles (id, name) VALUES (2, 'EDIT_EVENT');
INSERT INTO roles (id, name) VALUES (3, 'VIEW_EVENT');

INSERT INTO usergroups (id, name) VALUES (1, 'ADMIN');
INSERT INTO usergroups (id, name) VALUES (2, 'USER');
INSERT INTO group_roles (group_id, role_id) VALUES (1, 1);
INSERT INTO group_roles (group_id, role_id) VALUES (1, 2);
INSERT INTO group_roles (group_id, role_id) VALUES (1, 3);

-- USER has only VIEW_EVENT
INSERT INTO group_roles (group_id, role_id) VALUES (2, 3);