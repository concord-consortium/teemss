# packaging-functions.sh
# variables and functions for the Package creation scripts

complete=../complete
download=../download
components=../components

bin=../../CCProbe/bin
doc=../doc
tempdir=../tempdir
labbook=../../TeemssXML/LabBookXSL

function hide-cvs
{
    mv $1/CVS $1/.CVS
}

function show-cvs
{
    mv $1/.CVS $1/CVS
}

function copy-mac-cvs-folder
{
    hide-cvs $1
    CpMac $1/* $2
    show-cvs $1
}

function copy-cvs-folder
{
    hide-cvs $1
    cp $1/* $2
    show-cvs $1
}

function windows-to-unix-newline
{
    tr -d '\r' <$1 >$2
}

function windows-to-macos-newline
{
    tr -d '\n' <$1 >$2
}
