create table public.wallets
(
    id         bigserial
        constraint wallets_pk
            primary key,
    user_id    bigint                                     not null
        constraint wallets_users_id_fk
            references public.users
            on delete cascade,
    balance    double precision default 0                 not null,
    currency   varchar(255)                               not null,
    created_at timestamptz      default current_timestamp not null
);

