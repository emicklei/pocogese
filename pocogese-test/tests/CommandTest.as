package tests
{
	import flexunit.framework.TestCase;
	import com.philemonworks.flex.net.Command;

	public class CommandTest extends TestCase
	{
		public function testCreate():void {
			var cmd:Command = new Command('testCreate', 'n1' , 'v1' , 'n2' , 'v2' , 'n3' , 'v3')
			assertNotNull(cmd)
			assertNotNull(cmd.toXML().toXMLString())
		}
		public function testNoArguments():void {
			var cmd:Command = new Command("zero")
			trace("zero=" + cmd.toXML().toXMLString())
		}
	}
}