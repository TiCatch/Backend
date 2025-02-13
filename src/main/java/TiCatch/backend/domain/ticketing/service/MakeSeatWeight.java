package TiCatch.backend.domain.ticketing.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MakeSeatWeight {

    public static final Map<String, Integer> SECTION_WEIGHTS = new HashMap<>();

    // 구역별 가중치 설정
    static {
        SECTION_WEIGHTS.put("SA", 400);
        SECTION_WEIGHTS.put("SE", 400);
        SECTION_WEIGHTS.put("SC", 395);
        SECTION_WEIGHTS.put("SB", 390);
        SECTION_WEIGHTS.put("SD", 390);
        SECTION_WEIGHTS.put("S8", 300);
        SECTION_WEIGHTS.put("S7", 300);
        SECTION_WEIGHTS.put("S9", 300);
        SECTION_WEIGHTS.put("S6", 270);
        SECTION_WEIGHTS.put("S10", 270);
        SECTION_WEIGHTS.put("S5", 260);
        SECTION_WEIGHTS.put("S11", 260);
        SECTION_WEIGHTS.put("S4", 250);
        SECTION_WEIGHTS.put("S12", 250);
        SECTION_WEIGHTS.put("S3", 240);
        SECTION_WEIGHTS.put("S13", 240);
        SECTION_WEIGHTS.put("S2", 240);
        SECTION_WEIGHTS.put("S14", 240);
        SECTION_WEIGHTS.put("S1", 230);
        SECTION_WEIGHTS.put("S15", 230);
        SECTION_WEIGHTS.put("S33", 180);
        SECTION_WEIGHTS.put("S34", 180);
        SECTION_WEIGHTS.put("S32", 170);
        SECTION_WEIGHTS.put("S35", 170);
        SECTION_WEIGHTS.put("S31", 160);
        SECTION_WEIGHTS.put("S36", 160);
        SECTION_WEIGHTS.put("S30", 150);
        SECTION_WEIGHTS.put("S37", 150);
        SECTION_WEIGHTS.put("S29", 140);
        SECTION_WEIGHTS.put("S38", 140);
        SECTION_WEIGHTS.put("S28", 130);
        SECTION_WEIGHTS.put("S39", 130);
        SECTION_WEIGHTS.put("S27", 120);
        SECTION_WEIGHTS.put("S40", 120);
        SECTION_WEIGHTS.put("S26", 100);
        SECTION_WEIGHTS.put("S41", 100);
        SECTION_WEIGHTS.put("S25", 90);
        SECTION_WEIGHTS.put("S42", 90);
        SECTION_WEIGHTS.put("S24", 70);
        SECTION_WEIGHTS.put("S43", 70);
    }

    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Integer>> sectionInfo = objectMapper.readValue(
                    new File("src/main/resources/section_info.json"),
                    new TypeReference<>() {}
            );

            Map<String, Integer> seatWeights = new TreeMap<>();

            for (Map.Entry<String, Map<String, Integer>> sectionEntry : sectionInfo.entrySet()) {
                String section = sectionEntry.getKey();
                Map<String, Integer> rowInfo = sectionEntry.getValue();

                int baseWeight = SECTION_WEIGHTS.getOrDefault(section, 0);

                for (Map.Entry<String, Integer> rowEntry : rowInfo.entrySet()) {
                    String row = rowEntry.getKey();
                    int seatCount = rowEntry.getValue();
                    int rowWeight = Math.max(40 - Integer.parseInt(row), 20);
                    int totalWeight = baseWeight + rowWeight;
                    for (int col = 1; col <= seatCount; col++) {
                        String seatKey = String.format("%s:R%s:C%d", section, row, col);
                        seatWeights.put(seatKey, totalWeight);
                    }
                }
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("src/main/resources/seat_weights.json"), seatWeights);
            System.out.println("좌석별 가중치 파일 생성 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
