package com.convstore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String dataDir = System.getProperty("user.dir") + "/data/";

    public <T> List<T> readData(String filename, TypeReference<List<T>> type) {
        try {
            File file = new File(dataDir + filename);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public <T> void writeData(String filename, List<T> data) {
        try {
            File file = new File(dataDir + filename);
            file.getParentFile().mkdirs();
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
