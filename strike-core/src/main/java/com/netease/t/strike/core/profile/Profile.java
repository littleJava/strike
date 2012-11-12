package com.netease.t.strike.core.profile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.netease.t.strike.core.common.ExceptionUtil;
import com.netease.t.strike.core.common.ProfileInitException;

@Root
// (name="profile")
public class Profile {

    // private long setupDuration = 10;
    //
    // private long warmupDuration = 0;
    //
    // private long runDuration = 0;
    //
    // private long cooldownDuration = 10;
    //
    // private long teardownDuration = 0;

    // private long startCollectionFrom = 0;

    // private long stopCollectionAfter = 0;
    public void reset(){
        id = null;
        profileContext = new ProfileContext();
    }
    private String id="";
    
    private String profileName;
    
    public String getId() {
        return id;
    }

    public String getMark() {
        return "/" + id + "/";
    }

    public Profile appendId(String id) {
        if (this.id == null || "".equals(this.id)) {
            this.id = profileName+"-"+id;
        } else {
            this.id = this.id + "-" + id;
        }
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
        this.id = profileName;
    }

    @Element
    private String name;
    @Element
    private String type;
    @Element
    private boolean dev = false;
    @Element
    private int loop = 1;

    @Path("survival-date")
    @Element(name = "from")
    private String fromDateStr;

    private long fromDateMilli;

    @Path("survival-date")
    @Element(name = "to")
    private String endDateStr;
    private long endDateMilli;

    @Path("jvm")
    @Element(name = "java")
    private String javaPath;

    @Path("jvm")
    @Element(name = "args")
    private String javaArgs;

    @Element(name = "impl")
    @Path("worker")
    private String taskImpl = null;

    @Element(name = "data-provider")
    @Path("worker")
    private String taskDataProviderImpl = "";

    @Attribute(required = true, name = "init")
    @Path("worker/number")
    private int threadInitCount = 10;

    @Attribute(name = "max",required=false)
    @Path("worker/number")
    private int threadMaxCount = 0;

    @Attribute(name = "incr",required = false)
    @Path("worker/number")
    private int threadIncrCount = 0;

    @Element(name = "timeout")
    @Path("worker")
    private long taskTimeout = 0;

    @ElementList(inline = true, name = "reportor")
    @Path("reportors")
    private List<ReportorConfig> reportorConfigs;

    private List<String> reportorNames;

    @Element(name = "stress-sample-interval")
    @Path("reportors")
    private long sampleInterval = 10;

    @Element(name = "stress-sample-count")
    @Path("reportors")
    private int sampleCount = 0;

    @Path("stop")
    @Element(name = "success-ratio-less")
    private String successRatioStr;

    private double successRatio;

    @Path("stop")
    @Element(name = "duration")
    private long duration;

    private String runIdentifier;

    private String reportDirectory = "target/basher-reports";

    private ExecutionType executionType;

    private Map<String, Object> taskParamContext = new HashMap<String, Object>(8);// task shared param

    private ProfileContext profileContext = null;

    public ProfileContext getProfileContext() {
        return profileContext;
    }

    public void setProfileContext(ProfileContext profileContext) {
        this.profileContext = profileContext;
    }

    /**
     * the absolute path of the profile in the disk
     */
    private String absolutePath;

    // private String includes;
    //
    // private String excludes;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Profile() {
        // reportorType = new ArrayList<String>();
        // reportorType.add("stdout");
    }

    /**
     * thie <code>com.netease.t.strike.core.task.Task</code> implementation class name
     * 
     * @param taskImpl
     */
    public void setTaskImpl(String taskImpl) {
        this.taskImpl = taskImpl;
    }

    /**
     * Retrieves the number of threads which the run should start with. Defaults to 10
     * 
     * @return The number of threads the run should start with
     */
    public int getThreadInitCount() {
        return threadInitCount;
    }

    /**
     * Sets the number of threads which the run should start with.<br/>
     * If not set, this defaults to 10
     * 
     * @param initialNumberThreads The number of threads the run should start with
     */

    public void setThreadInitCount(int threadInitCount) {
        this.threadInitCount = threadInitCount;
    }

    /**
     * Retrieves the maximum number of threads allowed in the run. Defaults to 0 (unlimited). This is mainly taken into
     * account in combination with {@link net.sourceforge.basher.BasherContext#setThreadIncrementCount(int)} and
     * {@link net.sourceforge.basher.BasherContext#setThreadIncrementInterval(int)}.
     * 
     * @return The number of threads the run should start with
     */
    public int getThreadMaxCount() {
        return threadMaxCount;
    }

    /**
     * Sets the maximum number of threads allowed in the run. This is mainly taken into account in combination with
     * {@link net.sourceforge.basher.BasherContext#setThreadIncrementCount(int)} and
     * {@link net.sourceforge.basher.BasherContext#setThreadIncrementInterval(int)}.<br/>
     * If not set, this defaults to 0 (unlimited).
     * 
     * @param threadMaxCount The number of threads the run should start with
     */
    public void setThreadMaxCount(int threadMaxCount) {
        this.threadMaxCount = threadMaxCount;
    }

    /**
     * Sets the number of threads should be added for each <i>threadIncrementInterval</i> allowed in the run.<br/>
     * If not set, this defaults to 0 (don't add any)
     * 
     * @return The number of threads to add
     */

    public int getThreadIncrCount() {
        return threadIncrCount;
    }

    /**
     * Retrieves the number of threads should be added for each <i>threadIncrementInterval</i> allowed in the run.<br/>
     * If not set, this defaults to 0 (don't add any)
     * 
     * @param threadIncrementCount The number of threads to add
     */

    public void setThreadIncrCount(int threadIncrCount) {
        this.threadIncrCount = threadIncrCount;
    }

    /**
     * Retrieves the interval between average calculations (in seconds).
     * 
     * @return The time between average calculations (in seconds)
     */
    public long getSampleInterval() {
        return sampleInterval;
    }

    /**
     * Sets the interval between average calculations (in seconds).
     * 
     * @param reportInterval The time between average calculations (in seconds)
     */

    public void setSampleInterval(long sampleInterval) {
        this.sampleInterval = sampleInterval;
    }

    /**
     * Retrieves the name of this Basher context.
     * 
     * @return The name of the context
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this Basher context.
     * 
     * @param name The name of the Basher context
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Retrieves the report directory to be used for the running.<br/>
     * If not set, this defaults to 'target/basher-reports'.
     * 
     * @return The report directory
     */
    public String getReportDirectory() {
        return reportDirectory;
    }

    /**
     * Sets the report directory to be used for the running.<br/>
     * If not set, this defaults to 'target/basher-reports'.
     * 
     * @param reportDirectory The report directory
     */
    public void setReportDirectory(final String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    /**
     * Retrieves the duration (in seconds) of the setup run. The length defined here defines how long tasks (and the
     * JVM) are run (and how 'warmed up' the JVM could be before starting statistics collection).<br/>
     * If not set, this default to 10 seconds.
     * 
     * @return The duration (in seconds) of the setup run
     */
    public long getSetupDuration() {
        return duration;
    }

    /**
     * Retrieves the profile duration (in seconds) of the run. The length defined here defines how long statistics are
     * collected for.<br/>
     * 
     * @return The duration (in seconds) of the run
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Sets the duration (in seconds) of the run. The length defined here defines how long statistics are collected for.<br/>
     * 
     * @param runDuration The duration (in seconds) of the run
     */
    public void setDuration(final long duration) {
        this.duration = duration;
    }

    /**
     * @return
     */
    public ExecutionType getExecutionType() {
        return executionType;
    }

    /**
     * @param executionType
     */
    public void setExecutionType(final ExecutionType executionType) {
        this.executionType = executionType;
    }

    /**
     * Retrieves the run identifier to use. If not specified, one will be generated.
     * 
     * @return The run identifier
     */
    public String getRunIdentifier() {
        return runIdentifier;
    }

    /**
     * Specifies the run identifier to use. If not set, one will be generated
     * 
     * @param runIdentifier The run identifier
     */
    public void setRunIdentifier(final String runIdentifier) {
        this.runIdentifier = runIdentifier;
    }

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }

    public String getTaskImpl() {
        return taskImpl;
    }

    public String getTaskDataProviderImpl() {
        return taskDataProviderImpl;
    }

    public void setTaskDataProviderImpl(String taskDataProviderImpl) {
        this.taskDataProviderImpl = taskDataProviderImpl;
    }

    public List<String> getReportors() {
        return reportorNames;
    }

    public List<ReportorConfig> getReportorConfig() {
        return reportorConfigs;
    }

    public double getReportDetailRatio(String reportName) {
        for (ReportorConfig reportor : reportorConfigs) {
            if (reportName.equals(reportor.getName())) {
                return reportor.getDetailRatio();
            }
        }
        return 0;
    }

    public Map<String, Object> getTaskParamContext() {
        return taskParamContext;
    }

    public Object getTaskParam(String key) {
        return taskParamContext.get(key);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public long getFromDateMilli() {
        return fromDateMilli;
    }

    public long getEndDateMilli() {
        return endDateMilli;
    }

    /**
     * profile is turn on
     * 
     * @return
     */
    public boolean isActive() {
        long currentTime = System.currentTimeMillis();
        return (fromDateMilli < currentTime) && (endDateMilli > currentTime);
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getJavaArgs() {
        return javaArgs;
    }

    public void setJavaArgs(String javaArgs) {
        this.javaArgs = javaArgs;
    }

    public double getSuccessRatio() {
        return successRatio;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public long getTaskTimeout() {
        return taskTimeout;
    }

    public void setTaskTimeout(long taskTimeout) {
        this.taskTimeout = taskTimeout;
    }

    public void afterPropertiesSet() {
        try {
            profileContext = new ProfileContext();
            /**
             * init the reportors
             */
            this.reportorNames = new ArrayList<String>(reportorConfigs.size());
            for (ReportorConfig reportor : reportorConfigs) {
                reportorNames.add(reportor.getName());
            }
            /**
             * init the survival date
             */
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -1);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (Strings.isNullOrEmpty(fromDateStr)) {
                fromDateMilli = calendar.getTimeInMillis();
            }

            else {
                fromDateMilli = df.parse(fromDateStr).getTime();
            }
            calendar.add(Calendar.YEAR, 100);
            if (Strings.isNullOrEmpty(endDateStr)) {
                endDateMilli = calendar.getTimeInMillis();
            } else {
                endDateMilli = df.parse(endDateStr).getTime();
            }

            if (sampleCount == 0) {
                sampleCount = threadInitCount;
            }
            for (ReportorConfig config : reportorConfigs) {
                String ratioStr = config.getDetailRatioStr();
                if (!Pattern.matches("^([0-9]{1,2}|100)%$", ratioStr))
                    throw ExceptionUtil.build(ratioStr + ", ratioStr must be range in 1%-100%",
                            ProfileInitException.class);
                double detailRatio = Double.valueOf(StringUtils.delete(ratioStr, "%")) / 100.00;
                config.setDetailRatio(detailRatio);
            }

            if ("stress".equalsIgnoreCase(type)) {
                threadIncrCount = 0;
                threadMaxCount = threadInitCount;
            } else if ("hit".equalsIgnoreCase(type)) {
                if (threadIncrCount == 0) {
                    threadIncrCount = threadInitCount;
                }
                if (threadMaxCount < threadInitCount) {
                    throw ExceptionUtil.build("threadMaxCount < threadInitCount", ProfileInitException.class);
                } else if (threadMaxCount > threadInitCount && threadIncrCount <= 0) {
                    throw ExceptionUtil.build("threadIncrCount <= 0", ProfileInitException.class);
                }
            }
            if (!Pattern.matches("^([0-9]{1,2}|100)%$", successRatioStr))
                throw ExceptionUtil.build(successRatioStr + ", successRatio must be range in 0%-100%",
                        ProfileInitException.class);
            else {
                successRatio = Double.valueOf(StringUtils.delete(successRatioStr, "%")) / 100.00;
            }
        } catch (Exception e) {
            throw ExceptionUtil.build("profile afterPropertiesSet error", e, ProfileInitException.class);
        }
    }

    public void setTested() {
        profileContext.setEnd();
    }

    public boolean isTested() {
        return profileContext.isEnd();
    }

    public void updateRoundId() {
        this.profileContext.updateRoundId();
    }

    public int getRoundId() {
        return profileContext.getRoundId();
    }

}
