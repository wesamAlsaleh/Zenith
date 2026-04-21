create table public.transactions
(
    id                 bigserial                             not null
        constraint transactions_pk
            primary key,
    sender_wallet_id   bigint                                not null
        constraint transactions_wallets_id_fk
            references public.wallets
            on delete set null,
    receiver_wallet_id bigint                                not null
        constraint transactions_wallets_id_fk_2
            references public.wallets
            on delete set null,
    amount             decimal     default 0.0               not null,
    status             varchar(20)                           not null,
    transaction_type   varchar(30)                           not null,
    created_at         timestamptz default current_timestamp not null
);

