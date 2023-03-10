package com.featureprobe.api.service

import com.featureprobe.api.auth.GuestAuthenticationProvider
import com.featureprobe.api.auth.GuestAuthenticationToken
import com.featureprobe.api.base.enums.OrganizationRoleEnum
import com.featureprobe.api.component.SpringBeanManager
import com.featureprobe.api.config.JWTConfig
import com.featureprobe.api.dao.entity.Dictionary
import com.featureprobe.api.dao.entity.Environment
import com.featureprobe.api.dao.entity.Member
import com.featureprobe.api.dao.entity.Organization
import com.featureprobe.api.dao.entity.OrganizationMember
import com.featureprobe.api.dao.entity.Project
import com.featureprobe.api.dao.repository.DictionaryRepository
import com.featureprobe.api.dao.repository.EnvironmentRepository
import com.featureprobe.api.dao.repository.MemberRepository
import com.featureprobe.api.dao.repository.OperationLogRepository
import com.featureprobe.api.dao.repository.OrganizationMemberRepository
import com.featureprobe.api.dao.repository.OrganizationRepository
import com.featureprobe.api.dao.repository.ProjectRepository
import com.featureprobe.api.dao.repository.PublishMessageRepository
import com.featureprobe.api.dao.repository.TargetingSketchRepository
import com.featureprobe.sdk.server.FeatureProbe
import org.hibernate.internal.SessionImpl
import org.hibernate.query.spi.NativeQueryImplementor
import org.springframework.context.ApplicationContext
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query

class GuestAuthenticationProviderSpec extends Specification {

    EntityManager entityManager

    MemberRepository memberRepository

    MemberIncludeDeletedService memberIncludeDeletedService

    OrganizationRepository organizationRepository

    OrganizationMemberRepository organizationMemberRepository

    MemberService memberService

    GuestService guestService

    OperationLogRepository operationLogRepository

    OperationLogService operationLogService

    ProjectRepository projectRepository

    EnvironmentRepository environmentRepository

    TargetingSketchRepository targetingSketchRepository

    PublishMessageRepository publishMessageRepository

    DictionaryRepository dictionaryRepository

    ChangeLogService changeLogService

    ProjectService projectService

    JWTConfig jWTConfig = new JWTConfig(guestDefaultPassword: "123")

    ApplicationContext applicationContext

    GuestAuthenticationProvider provider

    def setup() {
        entityManager = Mock(SessionImpl)
        memberRepository = Mock(MemberRepository)
        memberIncludeDeletedService = new MemberIncludeDeletedService(memberRepository, entityManager)
        organizationRepository = Mock(OrganizationRepository)
        organizationMemberRepository = Mock(OrganizationMemberRepository)
        memberService = new MemberService(memberRepository, memberIncludeDeletedService, organizationRepository, organizationMemberRepository, entityManager)
        projectRepository = Mock(ProjectRepository)
        environmentRepository = Mock(EnvironmentRepository)
        targetingSketchRepository = Mock(TargetingSketchRepository)
        publishMessageRepository = Mock(PublishMessageRepository)
        dictionaryRepository = Mock(DictionaryRepository)
        changeLogService = new ChangeLogService(publishMessageRepository, environmentRepository, dictionaryRepository)
        projectService = new ProjectService(projectRepository, environmentRepository, targetingSketchRepository, changeLogService, entityManager)
        guestService = new GuestService(jWTConfig, memberRepository, organizationRepository, entityManager, projectService)
        operationLogRepository = Mock(OperationLogRepository)
        operationLogService = new OperationLogService(operationLogRepository)
        applicationContext = Mock(ApplicationContext)
        SpringBeanManager.applicationContext = applicationContext
        provider = new GuestAuthenticationProvider(memberService, guestService, operationLogService)
    }

    def "authenticate token is exist"() {
        given:
        def account = "Test"
        GuestAuthenticationToken authenticationToken = new GuestAuthenticationToken(account, "demo", "123")
        when:
        def authenticate = provider.authenticate(authenticationToken)
        then:
        2 * memberRepository.findByAccount(account) >> Optional.of(new Member(account: account))
        1 * memberRepository.save(_)
        1 * operationLogRepository.save(_)
    }

    def "authenticate token not exist"() {
        given:
        Query query = Mock(NativeQueryImplementor)
        GuestAuthenticationToken authenticationToken = new GuestAuthenticationToken("Admin", "demo", "123")
        when:
        def authenticate = provider.authenticate(authenticationToken)
        then:
        1 * memberRepository.findByAccount("Admin") >> Optional.empty()
        1 * applicationContext.getBean(_) >> new FeatureProbe("_")
        1 * memberRepository.save(_) >> new Member(id: 1, account: "Admin",
                organizationMembers: [new OrganizationMember(role: OrganizationRoleEnum.OWNER)])
        1 * organizationRepository.save(_) >> new Organization(name: "Admin")
        1 * projectRepository.count() >> 2
        1 * projectRepository.save(_) >> new Project(name: "projectName", key: "projectKey",
                environments: [new Environment()])
        1 * dictionaryRepository.findByKey(_) >> Optional.of(new Dictionary(value: "1"))
        1 * dictionaryRepository.save(_)
        1 * publishMessageRepository.save(_)
        entityManager.createNativeQuery(_) >> query
        20 * query.executeUpdate()
        1 * operationLogRepository.save(_)
        null != authenticate
    }


    def "provider supports"() {
        given:
        def account = "Test"
        GuestAuthenticationToken authenticationToken = new GuestAuthenticationToken(account, "demo", "123")
        when:
        def supports = provider.supports(authenticationToken.class)
        then:
        supports
    }
}

