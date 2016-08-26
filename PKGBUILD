pkgname=$(basename $(pwd))
pkgver=2.10.1
pkgrel=1
pkgdesc='InfoGrid'
arch=('any')
url="http://infogrid.org/"
license=('AGPL')
makedepends=(
    'maven'
    'jdk8-openjdk')
depends=(
    'java-runtime'
    'diet4j')

prepare() {
    # Set pom.xml versions correctly; depends on XML-comment-based markup in pom.xml files
    find ${startdir} -name pkg -and -type d -prune -or -name pom.xml -exec perl -pi -e "s/(?<=\<\!-- PKGVER -->)(\d+(\.\d+)+)(?=\<\!-- \/PKGVER -->)/${pkgver}/g" {} \;
}

build() {
    cd ${startdir}
    mvn package install ${MVN_OPTS}
}

package() {
    installJar 'infogrid-graphdb-grid' 'org.infogrid.kernel.net'
    installJar 'infogrid-graphdb-grid' 'org.infogrid.meshbase.store.net'
    installJar 'infogrid-graphdb-grid' 'org.infogrid.scene'

    installJar 'infogrid-graphdb' 'org.infogrid.codegen'
    installJar 'infogrid-graphdb' 'org.infogrid.kernel'
    installJar 'infogrid-graphdb' 'org.infogrid.kernel.active'
    installJar 'infogrid-graphdb' 'org.infogrid.meshbase.security.aclbased'
    installJar 'infogrid-graphdb' 'org.infogrid.meshbase.store'
    installJar 'infogrid-graphdb' 'org.infogrid.model.Test'

    installJar 'infogrid-lid' 'org.infogrid.lid'
    installJar 'infogrid-lid' 'org.infogrid.lid.ldap'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.account'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.formatnegotiation'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.lid'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.openid.auth'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.post'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.traversal'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.xrd'
    installJar 'infogrid-lid' 'org.infogrid.lid.model.yadis'
    installJar 'infogrid-lid' 'org.infogrid.lid.openid'
    installJar 'infogrid-lid' 'org.infogrid.lid.openid.store'
    installJar 'infogrid-lid' 'org.infogrid.lid.store'

    installJar 'infogrid-model-library' 'org.infogrid.model.Annotation'
    installJar 'infogrid-model-library' 'org.infogrid.model.Blob'
    installJar 'infogrid-model-library' 'org.infogrid.model.Bookmark'
    installJar 'infogrid-model-library' 'org.infogrid.model.Common'
    installJar 'infogrid-model-library' 'org.infogrid.model.Feeds'
    installJar 'infogrid-model-library' 'org.infogrid.model.Requirement'
    installJar 'infogrid-model-library' 'org.infogrid.model.Tagging'
    installJar 'infogrid-model-library' 'org.infogrid.model.VCard'
    installJar 'infogrid-model-library' 'org.infogrid.model.Web'
    installJar 'infogrid-model-library' 'org.infogrid.model.Wiki'

    installJar 'infogrid-probe' 'org.infogrid.model.Probe'
    installJar 'infogrid-probe' 'org.infogrid.net.local'
    installJar 'infogrid-probe' 'org.infogrid.net.local.store'
    installJar 'infogrid-probe' 'org.infogrid.probe'
    installJar 'infogrid-probe' 'org.infogrid.probe.feeds'
    installJar 'infogrid-probe' 'org.infogrid.probe.store'
    installJar 'infogrid-probe' 'org.infogrid.probe.vcard'
    installJar 'infogrid-probe' 'org.infogrid.probe.xrd'

    installJar 'infogrid-stores' 'org.infogrid.store'
    installJar 'infogrid-stores' 'org.infogrid.store.filesystem'
    installJar 'infogrid-stores' 'org.infogrid.store.keystore'
    installJar 'infogrid-stores' 'org.infogrid.store.sql'
    installJar 'infogrid-stores' 'org.infogrid.store.sql.mysql'
    installJar 'infogrid-stores' 'org.infogrid.store.sql.postgresql'

    installJar 'infogrid-ui' 'org.infogrid.jee'
    installJar 'infogrid-ui' 'org.infogrid.jee.lid'
    installJar 'infogrid-ui' 'org.infogrid.jee.net.testapp'
    installJar 'infogrid-ui' 'org.infogrid.jee.probe'
    installJar 'infogrid-ui' 'org.infogrid.jee.security.aclbased'
    installJar 'infogrid-ui' 'org.infogrid.jee.shell.http'
    installJar 'infogrid-ui' 'org.infogrid.jee.templates'
    installJar 'infogrid-ui' 'org.infogrid.jee.testapp'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet.bulk'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet.json'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet.log4j'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet.net'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet.net.local'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet.net.local.store'
    installJar 'infogrid-ui' 'org.infogrid.jee.viewlet.store'
    installJar 'infogrid-ui' 'org.infogrid.model.Viewlet'
    installJar 'infogrid-ui' 'org.infogrid.viewlet'

    installJar 'infogrid-utils' 'org.infogrid.comm.pingpong'
    installJar 'infogrid-utils' 'org.infogrid.comm.smtp'
    installJar 'infogrid-utils' 'org.infogrid.comm'
    installJar 'infogrid-utils' 'org.infogrid.crypto'
    installJar 'infogrid-utils' 'org.infogrid.httpd.filesystem'
    installJar 'infogrid-utils' 'org.infogrid.httpd'
    installJar 'infogrid-utils' 'org.infogrid.testharness'
    installJar 'infogrid-utils' 'org.infogrid.util.instrument'
    installJar 'infogrid-utils' 'org.infogrid.util.logging.log4j'
    installJar 'infogrid-utils' 'org.infogrid.util.sql'
    installJar 'infogrid-utils' 'org.infogrid.util'
}

installJar() {
    local project=$1
    local name=$2
    install -m644 -D ${startdir}/${project}/${name}/target/${name}-${pkgver}.jar ${pkgdir}/usr/lib/java/org/infogrid/${name}/${pkgver}/${name}-${pkgver}.jar
}
