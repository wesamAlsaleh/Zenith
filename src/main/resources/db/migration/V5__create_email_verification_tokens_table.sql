create table public.email_verification_tokens
(
    id         bigserial
        constraint email_verification_tokens_pk
            primary key,
    user_id    bigint                                not null,
    token      text                                  not null,
    used       boolean     default false             not null,
    expired_at timestamptz                           not null,
    created_at timestamptz default current_timestamp not null
);

