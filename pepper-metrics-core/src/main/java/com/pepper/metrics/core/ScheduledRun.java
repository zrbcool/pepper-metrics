package com.pepper.metrics.core;

import com.pepper.metrics.core.extension.Scope;
import com.pepper.metrics.core.extension.Spi;

import java.util.Set;
/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 * @description
 * 会被主调度流程定期调度，由于是同步执行，该扩展实现不可长时间阻塞线程
 */
@Spi(scope = Scope.SINGLETON)
public interface ScheduledRun {
    public void run(Set<Stats> statsSet);
}
