alter table public.verification_tokens
    rename constraint verification_tokens_pk to user_sessions_pk;

alter table public.verification_tokens
    rename constraint verification_tokens_users_id_fk to user_sessions_users_id_fk;

alter table public.verification_tokens
    rename to user_sessions;

