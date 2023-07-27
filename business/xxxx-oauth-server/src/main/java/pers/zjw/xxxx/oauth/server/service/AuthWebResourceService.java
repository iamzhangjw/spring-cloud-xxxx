package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.constant.ResourceType;
import pers.zjw.xxxx.oauth.server.mapper.AuthWebResourceMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthWebResource;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthWebResourceForm;
import pers.zjw.xxxx.oauth.server.pojo.vo.WebResource;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.exception.BizException;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * web资源表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Service
public class AuthWebResourceService extends ServiceImpl<AuthWebResourceMapper, AuthWebResource> {
    private static final long ROOT_ID = 0;

    @Autowired
    private AuthWebResourceMapper mapper;

    public Collection<String> types() {
        return Stream.of(ResourceType.values()).map(Enum::name).collect(Collectors.toSet());
    }

    public void save(AuthWebResourceForm data, AuthenticatedUser user) {
        AuthWebResource entity = new AuthWebResource();
        entity.setName(data.getName());
        entity.setDisplayName(data.getDisplayName());
        entity.setUri(data.getUri());
        entity.setEnabled(data.getEnabled());
        entity.setOrderNum(data.getOrderNum());
        entity.setType(data.getType());
        if (null != data.getParentId()) {
            entity.setParentId(data.getParentId());
            AuthWebResource parent = super.getById(data.getParentId());
            if (null != parent) {
                entity.setPath(parent.getPath() + "-" + parent.getId());
            }
        }
        long now = System.currentTimeMillis();
        entity.setVersion(now);
        if (null == data.getId()) {
            if (exists(data.getCode())) throw new BizException(ErrorCode.DATA_DUPLICATION, "code重复");
            entity.setCode(data.getCode());
            entity.setCreateAt(now);
            entity.setCreateBy(user.getId());
            mapper.insert(entity);
        } else {
            entity.setModifyAt(now);
            entity.setModifyBy(user.getId());
            entity.setId(data.getId());
            mapper.updateById(entity);
        }
    }

    private boolean exists(String code) {
        return mapper.selectCount(Wrappers.lambdaQuery(AuthWebResource.class).eq(AuthWebResource::getCode, code)) > 0;
    }

    public WebResource getById(Long id) {
        return mapper.getById(id);
    }

    public WebResource getByCode(String code) {
        return mapper.getByCode(code);
    }

    public Set<WebResource> all() {
        return cascade(ROOT_ID, types());
    }

    private Set<WebResource> cascade(Long id, Collection<String> types) {
        if (null == id) return null;
        Collection<WebResource> all = leaves(id, types);
        Set<WebResource> result = new TreeSet<>(Comparator.comparing(WebResource::getOrderNum));
        Map<Long, WebResource> map = all.stream().collect(Collectors.toMap(WebResource::getId, r -> r));
        map.forEach((k,v) -> {
            WebResource parent = map.get(v.getParentId());
            if (null == parent) {
                result.add(v);
            } else {
                Set<WebResource> leaves = parent.getLeaves();
                if (null == leaves) {
                    leaves = new TreeSet<>(Comparator.comparing(WebResource::getOrderNum));
                    parent.setLeaves(leaves);
                }
                leaves.add(v);
            }
        });
        return result;
    }

    private Collection<WebResource> leaves(Long id, Collection<String> types) {
        Collection<WebResource> list = new LinkedList<>();
        String path = null;
        if (ROOT_ID != id) {
            AuthWebResource root = super.getById(id);
            path = root.getPath() + "-" + root.getId();
            list.add(WebResource.of(root));
        }
        list.addAll(mapper.leaves(path, types));
        return list;
    }
}
