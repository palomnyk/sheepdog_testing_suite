package sheepdog;

import biolockj.Config;
import biolockj.Constants;
import biolockj.exception.FatalExceptionHandler;
import biolockj.util.DockerUtil;
import biolockj.util.MasterConfigUtil;
import biolockj.util.NextflowUtil;
import biolockj.util.SummaryUtil;

public class MockMain extends biolockj.BioLockJ
{
	private static String COMPLETE = "BioLockJ_Complete";
	protected static String RESULT_KEY = "HERE_IS_THE_RESULT__";
	protected static String PIPELINE_KEY = "HERE_IS_THE_PIPELINE__";
	
	public static void main(String args[]) {
		System.err.println( "Testing BioLockj..." + args[5] + " ... " + Constants.APP_START_TIME );
		String result = "default String";
		try {
			initBioLockJ( args );
			System.out.println( PIPELINE_KEY + Config.getPipelineDir().getAbsolutePath() );
			runPipeline();
			result = COMPLETE;
		} catch( final Exception ex ) {
			result = ex.getClass().getSimpleName();
			FatalExceptionHandler.logFatalError( args, ex );
		} finally {
			System.out.println( RESULT_KEY + result );
			if( !DockerUtil.isDirectMode() ) pipelineShutDown();
		}
	}
	
	protected static void pipelineShutDown() {

		setPipelineSecurity();

		if( DockerUtil.inAwsEnv() ) {
			NextflowUtil.saveNextflowLog();
			NextflowUtil.stopNextflow();
		}

		if( isPipelineComplete() ) {
			MasterConfigUtil.sanitizeMasterConfig();
			if( DockerUtil.inAwsEnv() ) NextflowUtil.saveNextflowSuccessFlag();
		}

		System.err.println( "Log Pipeline Summary..." + Constants.RETURN + SummaryUtil.getSummary() + SummaryUtil.displayAsciiArt() );
		if( isPipelineComplete() ) System.exit( 0 );

		System.exit( 1 );
	}
	
}