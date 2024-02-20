alter table word_count add column if not exists in_games bigint default 1;
alter table word_count add column if not exists smoothness double precision;