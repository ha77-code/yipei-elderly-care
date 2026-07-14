USE yipei;

INSERT INTO sys_user
(username, password, nickname, phone, role)
VALUES
    ('customer01', '123456', '测试家属', '13800000001', 'CUSTOMER'),
    ('companion01', '123456', '测试陪诊师', '13800000002', 'COMPANION'),
    ('admin01', '123456', '系统管理员', '13800000003', 'ADMIN');