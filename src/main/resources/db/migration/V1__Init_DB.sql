--create sequence hibernate_sequence start 1 increment 1;
create table users
(
    id int8 generated by default as identity,
    is_moderator boolean not null,
    reg_time  timestamp with time zone not null,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    code varchar(255),
    photo TEXT,
    primary key (id)
);

create table posts
(
    id int8 generated by default as identity,
    is_active boolean not null,
    moderation_status varchar(255) not null,--ENUM("NEW", "ACCEPTED", "DECLINED") NOT NUL
    moderator_id int8,
    user_id int8,
    time timestamp with time zone not null,
    title varchar(255) not null,
    text TEXT not null,
    view_count int4 not null,
    primary key (id)
);

create table post_votes
(
    id int8 generated by default as identity,
    user_id int8,
    post_id int8,
    time timestamp with time zone not null,
    value int4 not null,
    primary key (id)
);

create table tags
(
    id int8 generated by default as identity,
    name varchar(255) not null,
    primary key (id)
);

create table tag2post
(
    id int8 generated by default as identity,
    post_id int8,
    tag_id int8,
    primary key (id)
);

create table post_comments
(
    id int8 generated by default as identity,
    parent_id int8,
    post_id int8,
    user_id int8,
    time timestamp with time zone not null,
    text TEXT not null,
    primary key (id)
);

create table captcha_codes
(
    id int8 generated by default as identity,
    time timestamp with time zone not null,
    code varchar(255) not null,
    secret_code varchar(255) not null,
    primary key (id)
);

create table global_settings
(
    id int8 generated by default as identity,
    code varchar(255) not null,
    name varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
);

alter table if exists post_comments
    add constraint FK_COMMENT_USER_ID
    foreign key (user_id) references users;
alter table if exists post_comments
    add constraint FK_COMMENT_PARENT_ID
    foreign key (parent_id) references post_comments;
alter table if exists post_comments
    add constraint FK_COMMENT_POST_ID
    foreign key (post_id) references posts;
alter table if exists post_votes
    add constraint FK_VOTES_POST_ID
    foreign key (post_id) references posts;
alter table if exists post_votes
    add constraint FK_VOTES_USER_ID
    foreign key (user_id) references users;
alter table if exists posts
    add constraint FK_POST_USER_ID
    foreign key (user_id) references users;
alter table if exists posts
    add constraint FK_POST_MODERATION_ID
    foreign key (moderator_id) references users;
alter table if exists tag2post
    add constraint FK_POST_POST_ID
    foreign key (post_id) references posts;
alter table if exists tag2post
    add constraint FK_POST_TAG_ID
    foreign key (tag_id) references tags;