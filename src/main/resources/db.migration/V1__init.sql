create table if not exists age_rating(
    name text primary key
);

create table if not exists publisher(
    name text primary key
);

create table if not exists genre(
    name text primary key
);

create table if not exists platform(
    name text primary key
);

create table if not exists block_word(
    word text primary key
);

create table if not exists game_type(
    name text primary key,
    description text not null
);

create table if not exists key_word(
    word text primary key,
    game_type text references game_type(name)
);

create table if not exists game_info (
    id serial primary key,
    name text not null,
    score smallint,
    release_date date,
    summary text,
    vector double precision[],
    rating text references age_rating(name) ,
    publisher text references publisher(name),
    genre text references genre(name),
    platform text
);

create table if not exists game_platform(
    game_id int,
    platform text,
    primary key (game_id, platform),
    constraint game_id_fk foreign key (game_id) references game(id),
    constraint platform_fk foreign key (platform) references platform(name)
)