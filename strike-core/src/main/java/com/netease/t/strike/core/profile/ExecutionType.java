package com.netease.t.strike.core.profile;

public class ExecutionType {
    private String type;
    private int defaultTaskInvocations;

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public int getDefaultTaskInvocations()
    {
        return defaultTaskInvocations;
    }

    public void setDefaultTaskInvocations(final int defaultTaskInvocations)
    {
        this.defaultTaskInvocations = defaultTaskInvocations;
    }


    public String toString()
    {
        return "ExecutionType{" +
                "type='" + type + '\'' +
                ", defaultTaskInvocations=" + defaultTaskInvocations +
                '}';
    }

    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final ExecutionType that = (ExecutionType) o;

        if (defaultTaskInvocations != that.defaultTaskInvocations)
        {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = 31 * result + defaultTaskInvocations;
        return result;
    }
}