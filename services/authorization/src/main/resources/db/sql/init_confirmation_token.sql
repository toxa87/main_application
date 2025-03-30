create table access_users.confirmation_token (
    confirmation_token_id uuid primary key default gen_random_uuid(),
    token varchar(4000) not null,
    expired_at timestamp,
    create_at timestamp default now(),
    user_id uuid not null,
    constraint f_ct_user_id foreign key (user_id) references access_users.users(user_id) on delete cascade

);