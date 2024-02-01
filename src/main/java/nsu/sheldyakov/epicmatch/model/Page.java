package nsu.sheldyakov.epicmatch.model;

import java.util.List;

public record Page(int totalPages, List<GameInfo> gameInfos) {}
