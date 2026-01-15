package com.mayday.auth.model.vo;

import lombok.Data;
import java.util.List;

/**
 * 部门树形节点 VO
 *
 * @author MayDay Auth Generator
 * @since 1.0.0
 */
@Data
public class DeptTreeVo {

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String label;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 子部门列表
     */
    private List<DeptTreeVo> children;

    /**
     * 是否禁用 (用于前端 TreeSelect)
     */
    private Boolean disabled;
}
