package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Dependency provider for <b>h2</b> with <b>com.h2database:h2</b> coordinates and
     * with version reference <b>h2</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getH2() {
        return create("h2");
    }

    /**
     * Dependency provider for <b>jjwtApi</b> with <b>io.jsonwebtoken:jjwt-api</b> coordinates and
     * with version reference <b>jjwt</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJjwtApi() {
        return create("jjwtApi");
    }

    /**
     * Dependency provider for <b>jjwtImpl</b> with <b>io.jsonwebtoken:jjwt-impl</b> coordinates and
     * with version reference <b>jjwt</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJjwtImpl() {
        return create("jjwtImpl");
    }

    /**
     * Dependency provider for <b>jjwtJasckson</b> with <b>io.jsonwebtoken:jjwt-jackson</b> coordinates and
     * with version reference <b>jjwt</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJjwtJasckson() {
        return create("jjwtJasckson");
    }

    /**
     * Dependency provider for <b>jpa</b> with <b>org.springframework.boot:spring-boot-starter-data-jpa</b> coordinates and
     * with <b>no version specified</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getJpa() {
        return create("jpa");
    }

    /**
     * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
     * with <b>no version specified</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getLombok() {
        return create("lombok");
    }

    /**
     * Dependency provider for <b>mapstruct</b> with <b>org.mapstruct:mapstruct</b> coordinates and
     * with version reference <b>mapStructVersion</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getMapstruct() {
        return create("mapstruct");
    }

    /**
     * Dependency provider for <b>mapstructProcessor</b> with <b>org.mapstruct:mapstruct-processor</b> coordinates and
     * with version reference <b>mapStructVersion</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getMapstructProcessor() {
        return create("mapstructProcessor");
    }

    /**
     * Dependency provider for <b>redis</b> with <b>org.springframework.boot:spring-boot-starter-data-redis</b> coordinates and
     * with <b>no version specified</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getRedis() {
        return create("redis");
    }

    /**
     * Dependency provider for <b>security</b> with <b>org.springframework.boot:spring-boot-starter-security</b> coordinates and
     * with <b>no version specified</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getSecurity() {
        return create("security");
    }

    /**
     * Dependency provider for <b>webmvc</b> with <b>org.springframework.boot:spring-boot-starter-web</b> coordinates and
     * with <b>no version specified</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getWebmvc() {
        return create("webmvc");
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>bootDependency</b> with value <b>1.1.4</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBootDependency() { return getVersion("bootDependency"); }

        /**
         * Version alias <b>h2</b> with value <b>2.2.224</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getH2() { return getVersion("h2"); }

        /**
         * Version alias <b>jjwt</b> with value <b>0.11.5</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJjwt() { return getVersion("jjwt"); }

        /**
         * Version alias <b>mapStructVersion</b> with value <b>1.6.0.Beta1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMapStructVersion() { return getVersion("mapStructVersion"); }

        /**
         * Version alias <b>springBoot</b> with value <b>3.2.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringBoot() { return getVersion("springBoot"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>bootDependency</b> with plugin id <b>io.spring.dependency-management</b> and
         * with version reference <b>bootDependency</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getBootDependency() { return createPlugin("bootDependency"); }

        /**
         * Plugin provider for <b>springBoot</b> with plugin id <b>org.springframework.boot</b> and
         * with version reference <b>springBoot</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getSpringBoot() { return createPlugin("springBoot"); }

    }

}
