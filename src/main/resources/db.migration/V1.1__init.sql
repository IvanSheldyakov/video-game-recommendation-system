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
    description text,
    vector double precision[],
    rating text references age_rating(name) ,
    publisher text references publisher(name),
    genre text references genre(name)
);

create table if not exists game_platform(
    game_id int,
    platform text,
    primary key (game_id, platform),
    constraint game_id_fk foreign key (game_id) references game_info(id),
    constraint platform_fk foreign key (platform) references platform(name)
);

create table if not exists word_count(
    word text,
    relative_frequency double precision,
    game_id integer references game_info(id),
    primary key (word, game_id)
);

CREATE OR REPLACE FUNCTION calculate_cosine_similarity(vectorA_str text, vectorB double precision[])
    RETURNS double precision AS $$
DECLARE
    dotProduct double precision := 0;
    i integer;
    vectorA double precision[];
BEGIN
    -- Преобразование строки в массив double precision[]
    vectorA := ARRAY(SELECT unnest(string_to_array(vectorA_str, ','))::double precision);

    IF array_length(vectorA, 1) != array_length(vectorB, 1) THEN
        RAISE EXCEPTION 'Vectors must be of the same length';
    END IF;

    FOR i IN 1..array_length(vectorA, 1) LOOP
            dotProduct := dotProduct + (vectorA[i] * vectorB[i]);
        END LOOP;

    RETURN dotProduct;
END;
$$ LANGUAGE plpgsql;

insert into game_type(name, description)
values ('Ощущение',
        'Удовольствие ощущения подразумевает использование ваших чувств. Смотреть на что-то красивое, слушать музыку, прикасаться к шелку, нюхать или пробовать вкусную еду – это всё удовольствия ощущения. Этот тип удовольствия передается в первую очередь посредством эстетики.'),
       ('Фантазия',
        'Это удовольствие от воображаемого мира и от представления себя тем, кем вы на самом деле не являетесь.'),
       ('Повествование',
        'Под удовольствием повествования подразумевается не совсем рассказ заданной, линейной истории. Имеется в виду драматическое развитие последовательности событий по самым различным сценариям.'),
       ('Сложность',
        'В некотором смысле, сложность является основополагающим удовольствием геймплея, так как сердцем каждой игры является проблема, которую необходимо решить. Для некоторых игроков этого удовольствия достаточно – но другим нужно больше.'),
       ('Товарищество',
        'Это удовольствие, связанное с дружбой, сотрудничеством и сообществом. Без сомнения, для некоторых игроков это самое главное.'),
       ('Открытие',
        'Понятие удовольствия открытия довольно обширное: каждый раз, когда вы ищете и находите что-то новое — вы совершаете открытие. Иногда это исследование игрового мира, а иногда – открытие скрытых свойств или удачной стратегии. Без сомнений, открытие нового – основное игровое удовольствие.'),
       ('Самовыражение',
        'Это удовольствие самовыражения и удовольствие создавать что-то новое. В прошлом в геймдизайне было принято игнорировать это удовольствие. Сегодня игрок может создавать своих собственных персонажей, строить собственные уровни и делиться ими с другими игроками. Часто “игровое самовыражение” имеет минимальное значение в процессе выполнения цели игры. Изменение образа персонажа не дает игрового преимущества, но для некоторых игроков это весомый аргумент в пользу игры.'),
       ('Принятие',
        'Это удовольствие присоединиться к кругу избранных – оставить реальный мир позади и стать частью нового, более увлекательного набора правил и значений. В известном смысле, все игры позволяют ощутить этот тип удовольствия, но к некоторым игровым мирам просто более приятно и интересно присоединяться, чем к другим. В некоторых играх вас заставляют погрузиться на время в фантазию; в других вы погружаетесь в фантазию непроизвольно, так, что ваш мозг без проблем проникает в игровой мир и остается там. В таких играх принятие другой реальности превращается в настоящее удовольствие.'),
       ('Завершение', 'Приятно доводить начатое до конца. Многие игры используют этот тип удовольствия.'),
       ('Радость по поводу чужого горя',
        'Чаще всего мы чувствуем это, когда справедливое наказание достигает некую нечестную особу. Это важный аспект соревновательных игр.'),
       ('Дарение',
        'Удовольствие от того, что ваш подарок или сюрприз сделал кого-то счастливым, нельзя сравнить ни с чем. Мы заворачиваем наши подарки в бумагу, чтобы подчеркнуть и усилить удивление. Вы получаете удовольствие не только от того, что другой человек счастлив, но и от того, что это вы сделали его счастливым.'),
       ('Юмор',
        'Две, на первый взгляд, не совместимые вещи, вдруг соединяются за счет какого-то невероятного сдвига в восприятии. Это сложно описать, но нельзя не заметить, когда это происходит. Странно, но это заставляет нас смеяться.'),
       ('Возможность', 'Это удовольствие заключается в наличии различных вариантов и возможности выбора среди них.'),
       ('Сюрприз', 'Мозг любит сюрпризы.'),
       ('Трепет',
        'У создателей американских горок есть поговорка, что “страх минус смерть равняется фану”. Трепет – это именно такой тип фана – вы испытываете страх, но чувствуете, что вам ничего не угрожает.'),
       ('Победа',
        'Это удовольствие от того, что вы достигли успеха там, где у вас изначально было мало шансов. Обычно это удовольствие сопровождается победными криками.') on conflict do nothing;


insert into key_word(word, game_type)
values ('visuals', 'Ощущение'),
       ('aesthetics', 'Ощущение'),
       ('atmosphere', 'Ощущение'),
       ('sensory', 'Ощущение'),
       ('immersive', 'Ощущение'),
       ('tactile', 'Ощущение'),
       ('ambience', 'Ощущение'),
       ('sensation', 'Ощущение'),
       ('soundscape', 'Ощущение'),
       ('harmony', 'Ощущение'),
       ('escapism', 'Фантазия'),
       ('imagination', 'Фантазия'),
       ('creativity', 'Фантазия'),
       ('mythical', 'Фантазия'),
       ('alternate', 'Фантазия'),
       ('worldbuilding', 'Фантазия'),
       ('enchantment', 'Фантазия'),
       ('magic', 'Фантазия'),
       ('dreamlike', 'Фантазия'),
       ('surreal', 'Фантазия'),
       ('storytelling', 'Повествование'),
       ('nonlinear', 'Повествование'),
       ('intrigue', 'Повествование'),
       ('subplot', 'Повествование'),
       ('narration', 'Повествование'),
       ('emotional', 'Повествование'),
       ('narrative', 'Повествование'),
       ('captivating', 'Повествование'),
       ('unpredictable', 'Повествование'),
       ('twist', 'Повествование'),
       ('challenge', 'Сложность'),
       ('skill', 'Сложность'),
       ('mastery', 'Сложность'),
       ('complexity', 'Сложность'),
       ('complicated', 'Сложность'),
       ('sophisticated', 'Сложность'),
       ('puzzle', 'Сложность'),
       ('tactics', 'Сложность'),
       ('reaction', 'Сложность'),
       ('difficult', 'Сложность'),
       ('companionship', 'Товарищество'),
       ('сooperation', 'Товарищество'),
       ('friendship', 'Товарищество'),
       ('teamwork', 'Товарищество'),
       ('social', 'Товарищество'),
       ('collaboration', 'Товарищество'),
       ('community', 'Товарищество'),
       ('support', 'Товарищество'),
       ('networking', 'Товарищество'),
       ('cooperative', 'Товарищество'),
       ('exploration', 'Открытие'),
       ('discovery', 'Открытие'),
       ('secrets', 'Открытие'),
       ('uncharted', 'Открытие'),
       ('hidden', 'Открытие'),
       ('investigation', 'Открытие'),
       ('insight', 'Открытие'),
       ('curiosity', 'Открытие'),
       ('opening', 'Открытие'),
       ('finding', 'Открытие'),
       ('expression', 'Самовыражение'),
       ('creation', 'Самовыражение'),
       ('customization', 'Самовыражение'),
       ('artistry', 'Самовыражение'),
       ('individuality', 'Самовыражение'),
       ('personalization', 'Самовыражение'),
       ('expressiveness', 'Самовыражение'),
       ('originality', 'Самовыражение'),
       ('art', 'Самовыражение'),
       ('craft', 'Самовыражение'),
       ('acceptance', 'Принятие'),
       ('inclusion', 'Принятие'),
       ('artificial', 'Принятие'),
       ('affiliation', 'Принятие'),
       ('integration', 'Принятие'),
       ('connection', 'Принятие'),
       ('simulated', 'Принятие'),
       ('believable', 'Принятие'),
       ('realistic', 'Принятие'),
       ('simulation', 'Принятие'),
       ('completion', 'Завершение'),
       ('accomplishment', 'Завершение'),
       ('closure', 'Завершение'),
       ('fulfillment', 'Завершение'),
       ('resolution', 'Завершение'),
       ('finality', 'Завершение'),
       ('achievement', 'Завершение'),
       ('ending', 'Завершение'),
       ('conclusion ', 'Завершение'),
       ('milestone', 'Завершение'),
       ('gloat', 'Радость по поводу чужого горя'),
       ('retribution', 'Радость по поводу чужого горя'),
       ('contest', 'Радость по поводу чужого горя'),
       ('payback', 'Радость по поводу чужого горя'),
       ('justice', 'Радость по поводу чужого горя'),
       ('reckoning', 'Радость по поводу чужого горя'),
       ('triumph', 'Радость по поводу чужого горя'),
       ('punishment', 'Радость по поводу чужого горя'),
       ('competitive', 'Радость по поводу чужого горя'),
       ('competition', 'Радость по поводу чужого горя'),
       ('gift', 'Дарение'),
       ('gifting', 'Дарение'),
       ('delight', 'Дарение'),
       ('sharing', 'Дарение'),
       ('contribution', 'Дарение'),
       ('present', 'Дарение'),
       ('altruism', 'Дарение'),
       ('donation', 'Дарение'),
       ('grant', 'Дарение'),
       ('kindness', 'Дарение'),
       ('humor', 'Юмор'),
       ('wit', 'Юмор'),
       ('amusement ', 'Юмор'),
       ('laughter', 'Юмор'),
       ('comedy', 'Юмор'),
       ('joke', 'Юмор'),
       ('ridiculous', 'Юмор'),
       ('satire', 'Юмор'),
       ('hilarity', 'Юмор'),
       ('comical', 'Юмор'),
       ('possibility', 'Возможность'),
       ('choice', 'Возможность'),
       ('potential', 'Возможность'),
       ('options', 'Возможность'),
       ('alternatives', 'Возможность'),
       ('opportunity', 'Возможность'),
       ('flexibility', 'Возможность'),
       ('variant', 'Возможность'),
       ('diversity', 'Возможность'),
       ('variety', 'Возможность'),
       ('surprise', 'Сюрприз'),
       ('unexpected ', 'Сюрприз'),
       ('revelation', 'Сюрприз'),
       ('astonishment', 'Сюрприз'),
       ('marvel', 'Сюрприз'),
       ('unpredictable', 'Сюрприз'),
       ('startling', 'Сюрприз'),
       ('shock', 'Сюрприз'),
       ('miracle', 'Сюрприз'),
       ('unanticipated', 'Сюрприз'),
       ('thrill', 'Трепет'),
       ('excitement', 'Трепет'),
       ('intensity', 'Трепет'),
       ('thrill', 'Трепет'),
       ('suspense', 'Трепет'),
       ('anticipation', 'Трепет'),
       ('fear', 'Трепет'),
       ('terror', 'Трепет'),
       ('scary', 'Трепет'),
       ('tension', 'Трепет'),
       ('overcoming', 'Победа'),
       ('victory', 'Победа'),
       ('conquest', 'Победа'),
       ('perseverance', 'Победа'),
       ('resilience', 'Победа'),
       ('success', 'Победа'),
       ('effort', 'Победа'),
       ('attempts', 'Победа'),
       ('trials', 'Победа'),
       ('attempt', 'Победа')
on conflict do nothing;



















