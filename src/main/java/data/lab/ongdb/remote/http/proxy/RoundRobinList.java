package data.lab.ongdb.remote.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.proxy.RoundRobinList
 * @Description: TODO
 * @date 2020/4/28 15:18
 */
public class RoundRobinList {
    private static Logger logger = LoggerFactory.getLogger(RoundRobinList.class);
    private final List<HttpAddress> elements;

    private Iterator<HttpAddress> iterator;
    //	private String message;
    private HttpServiceHosts httpServiceHosts;

    public RoundRobinList(HttpServiceHosts httpServiceHosts, List<HttpAddress> elements) {
        this.elements = elements;
//		message = "All Http Server "+elements.toString()+" can't been connected.";
        iterator = this.elements.iterator();
        this.httpServiceHosts = httpServiceHosts;
    }


    public void addAddresses(List<HttpAddress> address) {
        try {
            lock.lock();
            this.elements.addAll(address);

            this.iterator = elements.iterator();
        } finally {
            lock.unlock();
        }
    }

    private Lock lock = new ReentrantLock();

    public HttpAddress get() {
        try {
            lock.lock();
            HttpAddress address = null;
            HttpAddress temp = null;
            while (iterator.hasNext()) {
                address = iterator.next();
                if (address.ok()) {
                    temp = address;
                    break;
                }
            }
            if (temp != null) {
                return temp;

            } else {
                iterator = elements.iterator();
                while (iterator.hasNext()) {
                    address = iterator.next();
                    if (address.ok()) {
                        temp = address;
                        break;
                    }
                }
                /**
                 if(temp == null) {
                 String message = new StringBuilder().append("All Http Server ").append(elements.toString()).append(" can't been connected.").toString();
                 throw new NoHttpServerException(message);
                 }
                 */
                return temp;
            }
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        if (elements != null)
            return elements.toString();
        return "[]";
    }

    public HttpAddress getOkOrFailed() {
        try {
            lock.lock();
            HttpAddress address = null;
            HttpAddress temp = null;
            while (iterator.hasNext()) {
                address = iterator.next();
                if (address.okOrFailed()) {
                    temp = address;
                    break;
                }
            }
            if (temp != null) {
                return temp;

            } else {
                iterator = elements.iterator();
                while (iterator.hasNext()) {
                    address = iterator.next();
                    if (address.okOrFailed()) {
                        temp = address;
                        break;
                    }
                }
                /**
                 if(temp == null) {
                 String message = new StringBuilder().append("All Http Server ").append(elements.toString()).append(" can't been connected.").toString();
                 throw new NoHttpServerException(message);
                 }
                 */
                return temp;
            }
        } finally {
            lock.unlock();
        }
    }

    public HttpAddress getOkOrFailedFromRouting() {
        try {
            lock.lock();
            HttpAddress address = null;
            HttpAddress temp = null;

            while (iterator.hasNext()) {

                address = iterator.next();
                if (address.okOrFailed()) {
                    temp = address;
                    break;
                }
            }
            if (temp != null) {
                return temp;

            } else {
                iterator = elements.iterator();
                while (iterator.hasNext()) {
                    address = iterator.next();
                    if (address.okOrFailed()) {
                        temp = address;
                        break;
                    }
                }

                if (temp == null && logger.isDebugEnabled()) {
                    String message = new StringBuilder().append("All Http Server ").append(elements.toString()).append(" can't been connected.").toString();
                    logger.debug(message);
                }
                return temp;
            }
        } finally {
            lock.unlock();
        }
    }

    public HttpAddress getFromRouting() {
        try {
            lock.lock();
            HttpAddress address = null;
            HttpAddress temp = null;

            while (iterator.hasNext()) {

                address = iterator.next();
                if (address.ok()) {
                    temp = address;
                    break;
                }
            }
            if (temp != null) {
                return temp;

            } else {
                iterator = elements.iterator();
                while (iterator.hasNext()) {
                    address = iterator.next();
                    if (address.ok()) {
                        temp = address;
                        break;
                    }
                }

                if (temp == null && logger.isDebugEnabled()) {
                    String message = new StringBuilder().append("All Http Server ").append(elements.toString()).append(" can't been connected.").toString();
                    logger.debug(message);
                }
                return temp;
            }
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        try {
            lock.lock();
            return elements.size();
        } finally {
            lock.unlock();
        }
    }
}
