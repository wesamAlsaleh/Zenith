alter table public.wallets
    add updated_at timestamptz default current_timestamp not null;

alter table public.wallets
    add constraint unique_user_currency_uk
        unique (user_id, currency);