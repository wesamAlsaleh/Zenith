alter table public.email_verification_tokens
drop constraint email_verification_tokens_users_id_fk;

alter table public.email_verification_tokens
    add constraint email_verification_tokens_users_id_fk
        foreign key (user_id) references public.users
            on delete cascade;

