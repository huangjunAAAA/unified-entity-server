package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class CID {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("guid")
    private String guid;

    public CID() {
    }

    public CID(Long id, String guid) {
        this.id = id;
        this.guid = guid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CID cid = (CID) o;
        return Objects.equals(id, cid.id) || Objects.equals(guid, cid.guid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guid);
    }
}