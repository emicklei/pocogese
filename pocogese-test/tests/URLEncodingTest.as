package tests
{
	import flexunit.framework.TestCase;
	import flash.net.URLVariables;

	public class URLEncodingTest extends TestCase
	{
		public function testEncode():void {
			var vars:URLVariables = new URLVariables()
			vars['a']=1
			vars['b']='<&>'
			vars['empty']=''
			vars['null']=null
			trace(vars)
		}
	}
}