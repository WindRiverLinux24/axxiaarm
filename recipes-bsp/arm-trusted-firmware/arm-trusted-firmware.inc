DESCRIPTION = "ARM Trusted Firmware"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://readme.md;md5=b7edbaf14bb1159f7218119fc6d47ef3"

PROVIDES = "virtual/arm-trusted-firmware"
SRC_URI[md5sum] = "a43d3f07023571af48a8729c31e0f652"

PACKAGE_ARCH = "${MACHINE_ARCH}"
DEPENDS += "u-boot-mkimage-native"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git/build"

BRANCH = "axxia-dev"
SRC_URI = "git://github.com/axxia/axxia_atf.git;protocol=https;branch=${BRANCH}"
FILESEXTRAPATHS:prepend:axxiaarm64 := "${THISDIR}/files:"
SRC_URI:append:axxiaarm64 = " \
           file://0001-fix-auth-forbid-junk-after-extensions.patch \
           file://0002-fix-auth-require-at-least-one-extension-to-be-presen.patch \
           file://0003-fix-auth-properly-validate-X.509-extensions.patch \
"

ATF_BASE_NAME ?= "${PN}-${PKGE}-${PKGV}-${PKGR}-${DATETIME}"
ATF_BASE_NAME[vardepsexclude] = "DATETIME"

COMPATIBLE_MACHINE = "^axxiaarm64$"
PLATFORM:axxiaarm64 = "axxia"
# requires CROSS_COMPILE set by hand as there is no configure script
export CROSS_COMPILE="${TARGET_PREFIX}"
OUTPUT_DIR = "${B}/${PLATFORM}/debug"

# Let the Makefile handle setting up the CFLAGS and LDFLAGS as it is a standalone application
CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

do_configure() {
	# Append --no-warn-rwx-segments to LDFLAGS
	sed -i 's/--gc-sections/& --no-warn-rwx-segments/' ${S}/Makefile
}

do_compile() {
	oe_runmake -C ${S} DEBUG=1 BUILD_BASE=${B} PLAT=${PLATFORM} USE_COHERENT_MEM=0 bl31
	${CROSS_COMPILE}objcopy -I binary -O elf64-littleaarch64 -B aarch64 --rename-section .data=.monitor ${OUTPUT_DIR}/bl31.bin ${OUTPUT_DIR}/bl31.o
}

do_install() {
	install -d ${D}/${libdir}
	install -m 0644 ${OUTPUT_DIR}/bl31.o ${D}/${libdir}/bl31.o
}
