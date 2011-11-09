package com.od.jtimeseries.server.util.path;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 06/11/11
* Time: 22:10
*/
public class PathMappingResult {

    private ResultType type = ResultType.PERMIT;
    private String newPath;

    public PathMappingResult(String newPath) {
        this.newPath = newPath;
    }

    public PathMappingResult(ResultType type, String newPath) {
        this.type = type;
        this.newPath = newPath;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public ResultType getType() {
        return type;
    }

    public String getNewPath() {
        return newPath;
    }

    public static enum ResultType {
        MIGRATE,
        DENY,
        PERMIT
    }

    @Override
    public String toString() {
        return "PathMappingResult{" +
                "type=" + type +
                ", newPath='" + newPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathMappingResult that = (PathMappingResult) o;

        if (newPath != null ? !newPath.equals(that.newPath) : that.newPath != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (newPath != null ? newPath.hashCode() : 0);
        return result;
    }
}
