package com.mayday.auth.service;

import com.mayday.auth.entity.SysDept;
import com.mayday.auth.mapper.SysDeptMapper;
import com.mayday.auth.model.vo.DeptTreeVo;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mayday.auth.entity.table.SysDeptTableDef.SYS_DEPT;

/**
 * 部门服务
 *
 * @author MayDay Auth Generator
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDeptService {

    private final SysDeptMapper deptMapper;

    /**
     * 获取部门树形结构
     *
     * @return 部门树列表 (顶级节点)
     */
    public List<DeptTreeVo> getDeptTree() {
        // 查询所有部门
        QueryWrapper wrapper = QueryWrapper.create()
                .from(SYS_DEPT)
                .orderBy(SYS_DEPT.ORDER_NUM.asc());

        List<SysDept> depts = deptMapper.selectListByQuery(wrapper);

        // 转换为 VO
        List<DeptTreeVo> allNodes = depts.stream()
                .map(this::toTreeVo)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allNodes, 0L);
    }

    /**
     * 构建树形结构
     *
     * @param allNodes 所有节点
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    private List<DeptTreeVo> buildTree(List<DeptTreeVo> allNodes, Long parentId) {
        // 按 parentId 分组
        Map<Long, List<DeptTreeVo>> childrenMap = allNodes.stream()
                .collect(Collectors.groupingBy(DeptTreeVo::getParentId));

        // 递归构建子树
        List<DeptTreeVo> roots = childrenMap.getOrDefault(parentId, new ArrayList<>());
        for (DeptTreeVo node : roots) {
            List<DeptTreeVo> children = buildTree(allNodes, node.getId());
            node.setChildren(children.isEmpty() ? null : children);
        }

        return roots;
    }

    /**
     * 实体转 VO
     */
    private DeptTreeVo toTreeVo(SysDept dept) {
        DeptTreeVo vo = new DeptTreeVo();
        vo.setId(dept.getId());
        vo.setLabel(dept.getDeptName());
        vo.setParentId(dept.getParentId());
        vo.setDisabled(false);
        return vo;
    }

    /**
     * 根据ID查询部门
     */
    public SysDept getById(Long id) {
        return deptMapper.selectOneById(id);
    }

    /**
     * 查询所有部门列表
     */
    public List<SysDept> listAll() {
        return deptMapper.selectAll();
    }
}
