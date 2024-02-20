create index if not exists game_info_vector_is_null_idx on game_info (vector) where game_info.vector is null;

create index if not exists game_info_vector_is_not_null_idx on game_info (vector) where game_info.vector is not null;