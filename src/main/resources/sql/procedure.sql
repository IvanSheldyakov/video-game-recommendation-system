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
