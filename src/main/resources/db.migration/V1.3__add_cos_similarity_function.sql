CREATE OR REPLACE FUNCTION calculate_cosine_similarity(vectorA_str text, vectorB double precision[])
    RETURNS double precision AS $$
DECLARE
    dotProduct double precision := 0;
    normA double precision := 0;
    normB double precision := 0;
    i integer;
    vectorA double precision[];
BEGIN
    -- Преобразование строки в массив double precision[]
    vectorA := ARRAY(SELECT unnest(string_to_array(vectorA_str, ','))::double precision);

    IF array_length(vectorA, 1) != array_length(vectorB, 1) THEN
        RAISE EXCEPTION 'Vectors must be of the same length';
    END IF;

    -- Вычисление нормы для vectorA
    FOR i IN 1..array_length(vectorA, 1) LOOP
            normA := normA + (vectorA[i] * vectorA[i]);
        END LOOP;
    normA := sqrt(normA);

    -- Вычисление нормы для vectorB
    FOR i IN 1..array_length(vectorB, 1) LOOP
            normB := normB + (vectorB[i] * vectorB[i]);
        END LOOP;
    normB := sqrt(normB);

    -- Проверка норм на ноль
    IF normA = 0 OR normB = 0 THEN
        RETURN 0; -- Возвращаем 0, если один из векторов является нулевым
    END IF;

    -- Вычисление скалярного произведения для нормированных векторов
    FOR i IN 1..array_length(vectorA, 1) LOOP
            dotProduct := dotProduct + ((vectorA[i] / normA) * (vectorB[i] / normB));
        END LOOP;

    -- Возвращаем косинусное сходство, которое в данном случае равно скалярному произведению нормированных векторов
    RETURN dotProduct;
END;
$$ LANGUAGE plpgsql;