#
# Autogenerated by Thrift Compiler (0.9.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#
require 5.6.0;
use strict;
use warnings;
use Thrift;

use Blur::Types;

# HELPER FUNCTIONS AND STRUCTURES

package Blur::BlurPlatform_execute_args;
use base qw(Class::Accessor);
Blur::BlurPlatform_execute_args->mk_accessors( qw( cluster request ) );

sub new {
  my $classname = shift;
  my $self      = {};
  my $vals      = shift || {};
  $self->{cluster} = undef;
  $self->{request} = undef;
  if (UNIVERSAL::isa($vals,'HASH')) {
    if (defined $vals->{cluster}) {
      $self->{cluster} = $vals->{cluster};
    }
    if (defined $vals->{request}) {
      $self->{request} = $vals->{request};
    }
  }
  return bless ($self, $classname);
}

sub getName {
  return 'BlurPlatform_execute_args';
}

sub read {
  my ($self, $input) = @_;
  my $xfer  = 0;
  my $fname;
  my $ftype = 0;
  my $fid   = 0;
  $xfer += $input->readStructBegin(\$fname);
  while (1) 
  {
    $xfer += $input->readFieldBegin(\$fname, \$ftype, \$fid);
    if ($ftype == TType::STOP) {
      last;
    }
    SWITCH: for($fid)
    {
      /^1$/ && do{      if ($ftype == TType::STRING) {
        $xfer += $input->readString(\$self->{cluster});
      } else {
        $xfer += $input->skip($ftype);
      }
      last; };
      /^2$/ && do{      if ($ftype == TType::STRUCT) {
        $self->{request} = new Blur::BlurCommandRequest();
        $xfer += $self->{request}->read($input);
      } else {
        $xfer += $input->skip($ftype);
      }
      last; };
        $xfer += $input->skip($ftype);
    }
    $xfer += $input->readFieldEnd();
  }
  $xfer += $input->readStructEnd();
  return $xfer;
}

sub write {
  my ($self, $output) = @_;
  my $xfer   = 0;
  $xfer += $output->writeStructBegin('BlurPlatform_execute_args');
  if (defined $self->{cluster}) {
    $xfer += $output->writeFieldBegin('cluster', TType::STRING, 1);
    $xfer += $output->writeString($self->{cluster});
    $xfer += $output->writeFieldEnd();
  }
  if (defined $self->{request}) {
    $xfer += $output->writeFieldBegin('request', TType::STRUCT, 2);
    $xfer += $self->{request}->write($output);
    $xfer += $output->writeFieldEnd();
  }
  $xfer += $output->writeFieldStop();
  $xfer += $output->writeStructEnd();
  return $xfer;
}

package Blur::BlurPlatform_execute_result;
use base qw(Class::Accessor);
Blur::BlurPlatform_execute_result->mk_accessors( qw( success ) );

sub new {
  my $classname = shift;
  my $self      = {};
  my $vals      = shift || {};
  $self->{success} = undef;
  $self->{ex} = undef;
  if (UNIVERSAL::isa($vals,'HASH')) {
    if (defined $vals->{success}) {
      $self->{success} = $vals->{success};
    }
    if (defined $vals->{ex}) {
      $self->{ex} = $vals->{ex};
    }
  }
  return bless ($self, $classname);
}

sub getName {
  return 'BlurPlatform_execute_result';
}

sub read {
  my ($self, $input) = @_;
  my $xfer  = 0;
  my $fname;
  my $ftype = 0;
  my $fid   = 0;
  $xfer += $input->readStructBegin(\$fname);
  while (1) 
  {
    $xfer += $input->readFieldBegin(\$fname, \$ftype, \$fid);
    if ($ftype == TType::STOP) {
      last;
    }
    SWITCH: for($fid)
    {
      /^0$/ && do{      if ($ftype == TType::STRUCT) {
        $self->{success} = new Blur::BlurCommandResponse();
        $xfer += $self->{success}->read($input);
      } else {
        $xfer += $input->skip($ftype);
      }
      last; };
      /^1$/ && do{      if ($ftype == TType::STRUCT) {
        $self->{ex} = new Blur::BlurException();
        $xfer += $self->{ex}->read($input);
      } else {
        $xfer += $input->skip($ftype);
      }
      last; };
        $xfer += $input->skip($ftype);
    }
    $xfer += $input->readFieldEnd();
  }
  $xfer += $input->readStructEnd();
  return $xfer;
}

sub write {
  my ($self, $output) = @_;
  my $xfer   = 0;
  $xfer += $output->writeStructBegin('BlurPlatform_execute_result');
  if (defined $self->{success}) {
    $xfer += $output->writeFieldBegin('success', TType::STRUCT, 0);
    $xfer += $self->{success}->write($output);
    $xfer += $output->writeFieldEnd();
  }
  if (defined $self->{ex}) {
    $xfer += $output->writeFieldBegin('ex', TType::STRUCT, 1);
    $xfer += $self->{ex}->write($output);
    $xfer += $output->writeFieldEnd();
  }
  $xfer += $output->writeFieldStop();
  $xfer += $output->writeStructEnd();
  return $xfer;
}

package Blur::BlurPlatformIf;

use strict;


sub execute{
  my $self = shift;
  my $cluster = shift;
  my $request = shift;

  die 'implement interface';
}

package Blur::BlurPlatformRest;

use strict;


sub new {
  my ($classname, $impl) = @_;
  my $self     ={ impl => $impl };

  return bless($self,$classname);
}

sub execute{
  my ($self, $request) = @_;

  my $cluster = ($request->{'cluster'}) ? $request->{'cluster'} : undef;
  my $request = ($request->{'request'}) ? $request->{'request'} : undef;
  return $self->{impl}->execute($cluster, $request);
}

package Blur::BlurPlatformClient;


use base qw(Blur::BlurPlatformIf);
sub new {
  my ($classname, $input, $output) = @_;
  my $self      = {};
  $self->{input}  = $input;
  $self->{output} = defined $output ? $output : $input;
  $self->{seqid}  = 0;
  return bless($self,$classname);
}

sub execute{
  my $self = shift;
  my $cluster = shift;
  my $request = shift;

    $self->send_execute($cluster, $request);
  return $self->recv_execute();
}

sub send_execute{
  my $self = shift;
  my $cluster = shift;
  my $request = shift;

  $self->{output}->writeMessageBegin('execute', TMessageType::CALL, $self->{seqid});
  my $args = new Blur::BlurPlatform_execute_args();
  $args->{cluster} = $cluster;
  $args->{request} = $request;
  $args->write($self->{output});
  $self->{output}->writeMessageEnd();
  $self->{output}->getTransport()->flush();
}

sub recv_execute{
  my $self = shift;

  my $rseqid = 0;
  my $fname;
  my $mtype = 0;

  $self->{input}->readMessageBegin(\$fname, \$mtype, \$rseqid);
  if ($mtype == TMessageType::EXCEPTION) {
    my $x = new TApplicationException();
    $x->read($self->{input});
    $self->{input}->readMessageEnd();
    die $x;
  }
  my $result = new Blur::BlurPlatform_execute_result();
  $result->read($self->{input});
  $self->{input}->readMessageEnd();

  if (defined $result->{success} ) {
    return $result->{success};
  }
  if (defined $result->{ex}) {
    die $result->{ex};
  }
  die "execute failed: unknown result";
}
package Blur::BlurPlatformProcessor;

use strict;


sub new {
    my ($classname, $handler) = @_;
    my $self      = {};
    $self->{handler} = $handler;
    return bless ($self, $classname);
}

sub process {
    my ($self, $input, $output) = @_;
    my $rseqid = 0;
    my $fname  = undef;
    my $mtype  = 0;

    $input->readMessageBegin(\$fname, \$mtype, \$rseqid);
    my $methodname = 'process_'.$fname;
    if (!$self->can($methodname)) {
      $input->skip(TType::STRUCT);
      $input->readMessageEnd();
      my $x = new TApplicationException('Function '.$fname.' not implemented.', TApplicationException::UNKNOWN_METHOD);
      $output->writeMessageBegin($fname, TMessageType::EXCEPTION, $rseqid);
      $x->write($output);
      $output->writeMessageEnd();
      $output->getTransport()->flush();
      return;
    }
    $self->$methodname($rseqid, $input, $output);
    return 1;
}

sub process_execute {
    my ($self, $seqid, $input, $output) = @_;
    my $args = new Blur::BlurPlatform_execute_args();
    $args->read($input);
    $input->readMessageEnd();
    my $result = new Blur::BlurPlatform_execute_result();
    eval {
      $result->{success} = $self->{handler}->execute($args->cluster, $args->request);
    }; if( UNIVERSAL::isa($@,'Blur::BlurException') ){ 
      $result->{ex} = $@;
    }
    $output->writeMessageBegin('execute', TMessageType::REPLY, $seqid);
    $result->write($output);
    $output->writeMessageEnd();
    $output->getTransport()->flush();
}

1;