create table access_users.access_token_blacklist (
    access_token varchar(4000) not null,
    expired_at timestamp
);