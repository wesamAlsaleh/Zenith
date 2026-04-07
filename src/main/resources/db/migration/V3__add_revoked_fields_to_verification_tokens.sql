alter table public.verification_tokens
    add revoked boolean default false not null;

alter table public.verification_tokens
    add revoked_at timestamptz;

