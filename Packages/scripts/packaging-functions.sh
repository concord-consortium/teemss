# packaging-functions.sh
# variables and functions for the Package creation scripts

complete=../complete
download=../download
components=../components

bin=../../CCProbe/bin
doc=../doc
tempdir=../tempdir
labbook=../../TeemssXML/LabBookXSL

function hidecvs
{
    mv $1/CVS $1/.CVS
}

function showcvs
{
    mv $1/.CVS $1/CVS
}

function copymaccvsfolder
{
    hidecvs $1
    CpMac $1/* $2
    showcvs $1
}

function copycvsfolder
{
    hidecvs $1
    cp $1/* $2
    showcvs $1
}

function WindowsToUnixNewline
{
    tr -d '\r' <$1 >$2
}

function WindowsToMacosNewline
{
    tr -d '\n' <$1 >$2
}
