package org.jeecf.manager.gen.handler;

import java.util.Map;

import org.jeecf.manager.common.chain.AbstractHandler;
import org.jeecf.manager.common.chain.ChainContext;
import org.jeecf.manager.gen.tools.ObjectTools;
import org.jeecf.manager.gen.tools.StringTools;

/**
 * 规则参数拦截器
 * 
 * @author jianyiming
 *
 */
public class ToolParamHandler extends AbstractHandler {

    private static final ObjectTools OBJECT_TOOLS = new ObjectTools();

    private static final StringTools STRING_TOOLS = new StringTools();

    @Override
    public void init(ChainContext context) {
        this.chainContext = context;
    }

    @Override
    public void process() {
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) this.chainContext.get("params");
        params.put("ObjectTools", OBJECT_TOOLS);
        params.put("StringTools", STRING_TOOLS);
        this.chainContext.put("params", params);
        this.chainContext.next();
    }
}
