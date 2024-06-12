#
# Copyright (C) 2014 Wind River Systems, Inc.
#
require recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"

# This revision corresponds to the tag "axxia_u-boot_5.8.1.96"
SRCREV = "f884431a69329dde6f5bd2e083ef13b52fee37e4"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

COMPATIBLE_MACHINE = "^axxiaarm$"
SRC_URI = "git://github.com/axxia/axxia_u-boot.git;protocol=https;branch=5500/axxia-dev"

SRC_URI:append = " file://0001-lsi-axm55xx-fix-the-u-boot-compile-fail.patch \
		   file://0008-compiler-.h-sync-include-linux-compiler-.h-with-Linu.patch \
		   file://0009-config.mk-add-Wno-address-of-packed-member-to-CFLAGS.patch \
		   file://0001-include-command.h-correct-to-use-enum-command_ret_t-.patch \
		 "

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"
UBOOT_MAKE_TARGET ?= "all"

EXTRA_OEMAKE += "AXXIA_VERSION=axxia_u-boot_5.8.1.96"
PV = "2013.01.01+git${SRCREV}"

# Some versions of u-boot use .bin and others use .img.  By default use .bin
# but enable individual recipes to change this value.
UBOOT_SUFFIX = "img"
UBOOT_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}.${UBOOT_SUFFIX}"
UBOOT_BINARY = "u-boot.${UBOOT_SUFFIX}"
UBOOT_SYMLINK = "u-boot-${MACHINE}.${UBOOT_SUFFIX}"

# SPL (Secondary Program Loader)
SPL_SUFFIX = "bin"
SPL_FILE = "u-boot-spl"
SPL_PATH = "spl"
SPL_BINARY = "${SPL_FILE}.${SPL_SUFFIX}"
SPL_IMAGE = "${SPL_FILE}-${MACHINE}-${PV}-${PR}.${SPL_SUFFIX}"
SPL_SYMLINK = "${SPL_FILE}-${MACHINE}.${SPL_SUFFIX}"

do_configure() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	oe_runmake -C ${S} O=${B} ${UBOOT_MACHINE}
}

do_compile() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_TARGET}
}

do_install () {
    install -d ${D}/boot
    install ${B}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
    ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}

    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi

    if [ "x${SPL_BINARY}" != "x" ]
    then
        install ${B}/${SPL_PATH}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
        ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARY}
    fi
}

do_deploy () {
    install -d ${DEPLOYDIR}
    install ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}

    cd ${DEPLOYDIR}
    rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}

    if [ "x${SPL_BINARY}" != "x" ]
    then
        install ${B}/${SPL_PATH}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
        rm -f ${DEPLOYDIR}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_SYMLINK}
        ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_FILE}.${SPL_SUFFIX}
        ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
    fi
}

