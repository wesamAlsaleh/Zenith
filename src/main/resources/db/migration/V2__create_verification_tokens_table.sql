create table public.verification_tokens
(
    id         bigserial
        constraint verification_tokens_pk
            primary key,
    token      text        not null
        constraint verification_tokens_token_unique_key
            unique,
    user_id    bigint      not null
        constraint verification_tokens_users_id_fk
            references public.users
            on delete cascade,
    created_at timestamptz default current_timestamp,
    expires_at timestamptz not null
);

comment on column public.verification_tokens.created_at is 'timestamptz to avoid timezone shift bugs';

comment on column public.verification_tokens.expires_at is 'timestamptz to avoid timezone shift bugs';

