create table access_users.refresh_token (
    refresh_token_id uuid primary key default gen_random_uuid(),
    refresh_token varchar(4000) not null,
    expired_at timestamp,
    create_at timestamp default now(),
    user_id uuid not null,
    revoked boolean,
    constraint f_rt_user_id foreign key (user_id) references access_users.users(user_id) on delete cascade

);