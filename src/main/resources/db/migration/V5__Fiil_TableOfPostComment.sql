INSERT INTO post_comments
    (post_id, parent_id, user_id, "time", text)
	VALUES (1, null, 1, '2019-06-02 05:30:24', 'testComment'),
	       (1, 1, 4, '2020-06-10 10:54:21', '<strong>Fyrklod</strong>, LOOOOL)))))'),
           (1, 1, 3, '2020-06-10 11:30:14', '<strong>Fyrklod</strong>, WTF????'),
           (1, 1, 5, '2020-06-10 11:30:14', '<strong>Fyrklod</strong>, Please clear comments for this post'),
           (12, null, 1, '2020-12-16 05:30:24', 'Can you write example?');