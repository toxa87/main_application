create table access_users.users (
    id uuid primary key default gen_random_uuid(),
    email varchar(255) unique not null,
    username varchar(255),
    password varchar(255) not null,
    create_at timestamp default now()
);