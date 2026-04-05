create table users
(
    id           bigserial                              not null
        constraint users_pk
            primary key,
    email        varchar(255)                        not null
        constraint users_pk_2
            unique,
    password     varchar(255)                        not null,
    is_enabled   boolean   default true              not null,
    role         varchar(20)                         not null,
    avatar_url   varchar(2048),
    phone_number varchar(8)                          not null,
    created_at   timestamp default current_timestamp not null,
    updated_at   timestamp default current_timestamp not null
);

