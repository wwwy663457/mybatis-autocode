<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${daoPackage}.${beanName}Mapper">

	<resultMap id="BaseResultMap" type="${modelPackage}.${beanName}">
    	<id column="${primKey.columnName}" jdbcType="${primKey.jdbcType}"  property="${primKey.attributeName}"  />
        <#list columns as item>
        	<#if primKey.columnName!=item.columnName>
        <result property="${item.attributeName}" jdbcType="${item.jdbcType}"  column="${item.columnName}" />  
        	</#if>  
        </#list>  
	</resultMap>

	<sql id="Base_Column_List">
		<#list columns as item>   
		${item.columnName}<#if item_has_next>,</#if>  
	    </#list>      
	</sql>

	<sql id="Alias_Column_List">
		<#list columns as item>
		t.${item.columnName}<#if item_has_next>,</#if> 
		</#list> 
	</sql>

	<sql id="Base_Condition">
		<where>
		<#if !deleteFlag>
			t.is_deleted = 0 
		</#if>
		<#list columns as item> 
			<#if primKey.columnName!=item.columnName&&item.columnName!=deleteColumn>  
	    <if test="${item.attributeName} != null"> 
	   		and t.${item.columnName} = ${'#'}{${item.attributeName},jdbcType=${item.jdbcType}}  
	    </if>
            </#if>  
        </#list>   
		<!-- 自定义条件-->
		</where>
	</sql>

	<!-- 根据条件查询-->
	<select id="selectAll" resultMap="BaseResultMap" parameterType="java.util.HashMap">
		select
		<include refid="Alias_Column_List" />
		from ${tableName} t
		<include refid="Base_Condition" />
	</select>

	<!-- 查询总数-->
	<select id="count" resultType="java.lang.Integer" parameterType="java.util.HashMap">
		select count(0)
		from ${tableName} t
		<include refid="Base_Condition" />
	</select>

	<!-- 根据主键查询-->
	<select id="selectByPrimaryKey" resultMap="BaseResultMap" parameterType="${primKey.fullAttributeType}">
		select
		<include refid="Base_Column_List" />
		from ${tableName}
		where  is_deleted = 0 and ${primKey.columnName} = ${'#'}{${primKey.attributeName},jdbcType=${primKey.jdbcType}}
	</select>


	<!-- 根据主键删除 -->
	<#if deleteFlag>
	<delete id="deleteByPrimaryKey" parameterType="${primKey.fullAttributeType}">
		delete from ${tableName}
		where ${primKey.columnName} = ${'#'}{${primKey.attributeName},jdbcType=${primKey.jdbcType}}
	</delete>
	<#else>  
    <update id="deleteByPrimaryKey" parameterType="${primKey.fullAttributeType}">
		update ${tableName} set is_deleted = 1
		where ${primKey.columnName} = ${'#'}{${primKey.attributeName},jdbcType=${primKey.jdbcType}} and is_deleted = 0
	</update>
	</#if>
	
	
	
	<!-- 插入所有字段 -->
	<insert id="insert" parameterType="${modelPackage}.${beanName}">
		<#if autoID>
		<!-- <selectKey resultType="${primKey.fullAttributeType}" order="AFTER" keyProperty="${primKey.attributeName}">
		SELECT LAST_INSERT_ID() AS ${primKey.attributeName}
		</selectKey>  -->
		</#if>
		insert into ${tableName}(
			<include refid="Base_Column_List" />
		)
		values(<#list columns as item>  
                ${'#'}{${item.attributeName},jdbcType=${item.jdbcType}}<#if item_has_next>,</#if> 
               </#list>)
	</insert>

	<!-- 插入部分字段 -->
	<insert id="insertSelective" parameterType="${modelPackage}.${beanName}">
		<#if autoID>
		<selectKey resultType="${primKey.fullAttributeType}" order="AFTER" keyProperty="${primKey.attributeName}">
		SELECT LAST_INSERT_ID() AS ${primKey.attributeName}
		</selectKey>
		</#if>
		insert into ${tableName}
		<trim prefix="(" suffix=")" suffixOverrides=",">
		  	<#list columns as item>  
	        <if test="${item.attributeName} != null ">  
	       		${item.columnName},
	        </if>  
       		</#list> 
		</trim>
		<trim prefix="values (" suffix=")" suffixOverrides=",">
			<#list columns as item>  
            <if test="${item.attributeName} != null">  
            	${'#'}{${item.attributeName},jdbcType=${item.jdbcType}},
            </if>  
        </#list> 
		</trim>
	</insert>

	<!-- 更新部分字段-->
	<update id="updateByPrimaryKeySelective" parameterType="${modelPackage}.${beanName}">
		update ${tableName}
		<set>
	    	<#list columns as item>
	    	<#if primKey.columnName!=item.columnName>  
	        <if test="${item.attributeName} != null ">  
	        	${item.columnName} = ${'#'}{${item.attributeName},jdbcType=${item.jdbcType}},  
	        </if>  
            </#if>
        	</#list> 
		</set>
		where ${primKey.columnName} = ${'#'}{${primKey.attributeName},jdbcType=${primKey.jdbcType}}
	</update>

	<!-- 更新所有字段 -->
	<update id="updateByPrimaryKey" parameterType="${modelPackage}.${beanName}">
		update ${tableName}
		set
		<#list columns as item>  
           <#if primKey.columnName!=item.columnName> 
           ${item.columnName} = ${'#'}{${item.attributeName},jdbcType=${item.jdbcType}}<#if item_has_next>,</#if>
           </#if>
        </#list> 
		where ${primKey.columnName} = ${'#'}{${primKey.attributeName},jdbcType=${primKey.jdbcType}}
	</update>

	<!-- 批量插入 -->
	<insert id="batchInsert" parameterType="java.util.List">
		insert into ${tableName}(
		<include refid="Base_Column_List" />
		)
		values
		<foreach collection="list" index="index" item="item" separator=",">
		(
			${'#'}{item.${primKey.attributeName}}
            <#list columns as item>
	            <#if primKey.columnName!=item.columnName> 
	        <choose>
	            <when test="item.${item.attributeName} != null">,${'#'}{item.${item.attributeName}}</when><otherwise>,default</otherwise>
	        </choose>
	            </#if>
            </#list>
		)
		</foreach>
	</insert>

	<!--批量更新-->
	<update id="batchUpdate" parameterType="java.util.List">
		<foreach collection="list" separator=";" item="item">
             update ${tableName}   set
			<trim prefix="WHERE" prefixOverrides=",">
			<#list columns as item>
				<#if primKey.columnName!=item.columnName>
					<choose><when test="item.${item.attributeName} != null">,${item.columnName} = ${'#'}{item.${item.attributeName}}</when><otherwise>,default</otherwise></choose>
				</#if>
			</#list>
			</trim>
			where   ${primKey.columnName} = ${'#'}{item.${primKey.attributeName}}
		</foreach>
	</update>

	<!-- 批量插入或更新 -->
	<update id="batchInsertOrUpdate" parameterType="java.util.List">
		insert into ${tableName}(
			<include refid="Base_Column_List" />
		)
		values
		<foreach collection="list" index="index" item="item" separator=",">
		(
			${'#'}{item.${primKey.attributeName}}
			  <#list columns as item>
				  <#if primKey.columnName!=item.columnName> 
			<choose><when test="item.${item.attributeName} != null">,${'#'}{item.${item.attributeName}}</when><otherwise>,default</otherwise></choose>
	              </#if>
              </#list> 
		)
		</foreach>
		on duplicate key update 
		<#list columns as item> 
		   <#if primKey.columnName!=item.columnName> 
		   ${item.columnName}=values(${item.columnName})<#if item_has_next>,</#if> 
		   </#if>
		</#list> 
	</update>
	
	<!-- 批量删除-->
	<#if  deleteFlag>
	<delete id="batchDelete" parameterType="java.util.List">
		delete from ${tableName} where ${primKey.columnName} in 
		<foreach collection="list" index="index" item="item" open="(" separator="," close=")"> ${'#'}{item}
		</foreach>
	</delete>
	<#else>
	<update id="batchDelete" parameterType="java.util.List">
	update ${tableName} set is_deleted = 1 where ${primKey.columnName} in 
		<foreach collection="list" index="index" item="item" open="(" separator="," close=")"> ${'#'}{item}
		</foreach>
	and is_deleted = 0
	</update>
	</#if>
	
	<!-- 自定义查询 -->
</mapper>
