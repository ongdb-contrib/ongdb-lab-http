package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.register
 * @Description: TODO(CLUSTER NODE ROLE)
 * @date 2020/4/29 14:56
 */
public enum Role {
    /**
     * CORE-集群部署分类
     **/
    CORE,

    /**
     * OTHER-其它未识别角色类型
     **/
    OTHER,

    /**
     * FOLLOWER-节点类型
     **/
    FOLLOWER,

    /**
     * LEADER-节点类型
     **/
    LEADER,

    /**
     * READ_REPLICA-集群部署分类-同时也是节点类型
     **/
    READ_REPLICA;
}

