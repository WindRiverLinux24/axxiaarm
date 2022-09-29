DESCRIPTION = "U-boot bootloader fw_printenv/setenv utils"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM:axxiaarm = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"
LIC_FILES_CHKSUM:axxiaarm64 = "file://README;beginline=1;endline=6;md5=157ab8408beab40cd8ce1dc69f702a6c"
SECTION = "bootloader"
DEPENDS = "mtd-utils"

# This revision corresponds to the tag "lsi_axxia_u-boot_5.8.1.90"
SRCREV:axxiaarm = "fca362a4628b8383f83660b7b7651f3d405161af"
SRCREV:axxiaarm64 = "a0d6f2ff4ce1bc50925a6f02bd9fab3099b96f84"

PV = "2013.01.01+git${SRCREV}"
UBOOT_MACHINE:axxiaarm64 = "axm5600_defconfig"
SRC_URI:axxiaarm = "git://github.com/lsigithub/lsi_axxia_uboot_public.git;nobranch=1"
SRC_URI:axxiaarm64 = "git://github.com/axxia/axxia_u-boot.git;branch=axxia-dev"

SRC_URI:append:axxiaarm = " file://0001-fw_env-fix-compile-error-of-fw_env.patch\
                "

SRC_URI:append:axxiaarm64 = " file://0001-tools-fix-cross-compiling-tools-when-HOSTCC-is-overr.patch\
			      file://0001-fw_env-fix-compile-error-of-fw_env.patch\
		 "

S = "${WORKDIR}/git"
INSANE_SKIP:${PN}:append = "already-stripped"
EXTRA_OEMAKE:axxiaarm = 'CROSS_COMPILE=${TARGET_PREFIX} HOSTCC="${CC} ${LDFLAGS}" HOSTSTRIP="true"'
EXTRA_OEMAKE:axxiaarm64 = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'

COMPATIBLE_HOST:axxiaarm64="aarch64"
inherit uboot-config
UBOOT_AXXIA_CONFIG:axxiaarm = "axxia-55xx_config"
UBOOT_AXXIA_CONFIG:axxiaarm64 = "axm5600_defconfig"

do_compile () {
  oe_runmake ${UBOOT_AXXIA_CONFIG}
  oe_runmake env
}

do_install () {
  install -d ${D}${base_sbindir}
  install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
  install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_setenv
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "(axxiaarm|axxiaarm64)"
