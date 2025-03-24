create table access_users.users (
    id uuid primary key ,
    email varchar(255) unique not null,
    username varchar(255),
    passwort varchar(255) not null,
    create_at timestamp default now()
)