package data.lab.ongdb.remote.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.proxy.HttpServiceHostsConfig
 * @Description: TODO
 * @date 2020/4/28 15:17
 */
public class HttpServiceHostsConfig {
    public String getAuthAccount() {
        return authAccount;
    }

    public void setAuthAccount(String authAccount) {
        this.authAccount = authAccount;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public long getHealthCheckInterval() {
        return healthCheckInterval;
    }

    public void setHealthCheckInterval(long healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }

    public String getDiscoverService() {
        return discoverService;
    }

    public void setDiscoverService(String discoverService) {
        this.discoverService = discoverService;
    }

    public String getExceptionWare() {
        return exceptionWare;
    }

    public void setExceptionWare(String exceptionWare) {
        this.exceptionWare = exceptionWare;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    private String authAccount;
    private String authPassword;
    private String health;
    private long healthCheckInterval = -1l;
    private String discoverService;
    private String exceptionWare;
    private String hosts;
    private long discoverServiceInterval = 10000l;
    private Boolean handleNullOrEmptyHostsByDiscovery;

    public void toString(StringBuilder log, ExceptionWare exceptionWareBean, HttpHostDiscover httpHostDiscover) {
        log.append(",http.authAccount=").append(authAccount);
        log.append(",http.authPassword=").append(authPassword);
        log.append(",http.hosts=").append(hosts);
        log.append(",http.health=").append(health);
        log.append(",http.healthCheckInterval=").append(healthCheckInterval);
        log.append(",http.discoverServiceInterval=").append(discoverServiceInterval);
        log.append(",http.handleNullOrEmptyHostsByDiscovery=").append(handleNullOrEmptyHostsByDiscovery);
        if (exceptionWare != null)
            log.append(",http.exceptionWare=").append(exceptionWare);
        else if (exceptionWareBean != null) {
            log.append(",http.exceptionWare=").append(exceptionWareBean.getClass().getCanonicalName());
        }
        if (discoverService != null)
            log.append(",http.discoverService=").append(discoverService);
        else if (httpHostDiscover != null) {
            log.append(",http.discoverService=").append(httpHostDiscover.getClass().getCanonicalName());
        }
    }

    public long getDiscoverServiceInterval() {
        return discoverServiceInterval;
    }

    public void setDiscoverServiceInterval(long discoverServiceInterval) {
        this.discoverServiceInterval = discoverServiceInterval;
    }

    public Boolean getHandleNullOrEmptyHostsByDiscovery() {
        return handleNullOrEmptyHostsByDiscovery;
    }

    public void setHandleNullOrEmptyHostsByDiscovery(Boolean handleNullOrEmptyHostsByDiscovery) {
        this.handleNullOrEmptyHostsByDiscovery = handleNullOrEmptyHostsByDiscovery;
    }
}
